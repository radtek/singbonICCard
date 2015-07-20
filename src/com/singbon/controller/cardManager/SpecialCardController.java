package com.singbon.controller.cardManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.comet4j.core.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.singbon.controller.BaseController;
import com.singbon.device.CommandCodeCardReader;
import com.singbon.device.FrameCardReader;
import com.singbon.device.TerminalManager;
import com.singbon.entity.CardAllInfo;
import com.singbon.entity.Company;
import com.singbon.entity.Device;
import com.singbon.entity.SysUser;
import com.singbon.entity.User;
import com.singbon.service.SysUserService;
import com.singbon.service.mainCard.SpecialCardService;
import com.singbon.service.system.CompanyService;
import com.singbon.util.StringUtil;

/**
 * 功能卡制作控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/cardManager/specialCard")
public class SpecialCardController extends BaseController {

	@Autowired
	public SysUserService sysUserService;
	@Autowired
	public SpecialCardService specialCardService;
	@Autowired
	public CompanyService companyService;

	/**
	 * 首页
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest request, Model model) {
		SysUser sysUser = (SysUser) request.getSession().getAttribute("sysUser");
		Company company = (Company) request.getSession().getAttribute("company");
		Device device = (Device) request.getSession().getAttribute("device");
		model.addAttribute("sysUser", sysUser);
		model.addAttribute("company", company);
		model.addAttribute("device", device);

		if (device != null) {
			model.addAttribute("sn", device.getSn());
			// 读卡机状态
			if (TerminalManager.getSNToSocketChannelList().containsKey(device.getSn())) {
				model.addAttribute("cardStatus", 1);
			} else {
				model.addAttribute("cardStatus", 0);
			}
		} else {
			model.addAttribute("cardStatus", 0);
		}
		String url = request.getRequestURI();
		model.addAttribute("base", url.replace("/index.do", ""));
		return url.replace(".do", "");
	}

	/**
	 * 出纳员列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/cashierList.do")
	public String cashierList(HttpServletRequest request, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		List<SysUser> list = this.sysUserService.selectCashierList(company.getId());
		model.addAttribute("list", list);
		String url = request.getRequestURI();
		model.addAttribute("base", url.replace("/index.jsp", ""));
		return StringUtil.requestPath(request, "cashierList");
	}

	/**
	 * 制出纳员
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/makeCashierCard.do", method = RequestMethod.POST)
	public void makeCashierCard(@ModelAttribute SysUser user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		Device device = (Device) request.getSession().getAttribute("device");
		String sn = device.getSn();
		int section = companyService.getSection(company.getId());
		PrintWriter p = null;

		int cardSNCount = getCardSNCount(company.getId(), user.getCardSN(), sn);
		if (cardSNCount > 0) {
			return;
		}
		SocketChannel socketChannel = TerminalManager.getSNToSocketChannelList().get(sn);
		if (socketChannel != null) {
			try {
				int cardNO = this.specialCardService.selectMaxCardNO(company.getId());
				user.setCardNO(cardNO);
				user.setStatus(1);
				this.specialCardService.makeCashierCard(company.getId(), device, socketChannel, user, CommandCodeCardReader.MakeCashierCard, section);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取重复物理卡号数量
	 * 
	 * @param companyId
	 * @param cardSN
	 * @param sn
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int getCardSNCount(int companyId, String cardSN, String sn) {
		int cardSNCount = this.specialCardService.selectCountByCardSN(companyId, cardSN);
		if (cardSNCount > 0) {
			Map map = new HashMap();
			map.put("'f1'", FrameCardReader.ExsitCardSN);
			String msg = JSONUtil.convertToJson(map);
			TerminalManager.getEngineInstance().sendToAll("c" + sn, msg);
			return 1;
		}
		return 0;
	}

	/**
	 * 命令处理
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/command.do", method = RequestMethod.POST)
	public void command(String comm, HttpServletRequest request, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		Device device = (Device) request.getSession().getAttribute("device");
		String sn = device.getSn();
		int section = TerminalManager.getSection(company.getId());
		// 获取读卡器状态
		if ("getCardReaderStatus".equals(comm)) {
			SocketChannel socketChannel = TerminalManager.getSNToSocketChannelList().get(sn);
			if (socketChannel != null) {
				try {
					TerminalManager.getCardReaderHeartStatus(socketChannel);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 关闭连接通道
		else if ("closeSocketChannel".equals(comm)) {
			try {
				TerminalManager.closeSocketChannel(sn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 制出纳卡初始化
		else if ("makeCashierCardInit".equals(comm)) {
			SocketChannel socketChannel = TerminalManager.getSNToSocketChannelList().get(sn);
			if (socketChannel != null) {
				try {
					// 获取基本信息区0块
					List<Integer> sectionBlocks = new ArrayList<Integer>();
					sectionBlocks.add(section * 10);
					TerminalManager.getCardInfo(socketChannel, device, CommandCodeCardReader.MakeCashierCard, sectionBlocks);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}