<!-- 授权管理 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	
</script>
<div class="tabs" currentIndex="0" eventType="click" style="margin: 1px;" >
	<div class="tabsHeader">
		<div class="tabsHeaderContent">
			<ul>
				<li><a href="${base }/group/index.do" class="j-ajax"><span>授权分组</span></a></li>
				<li><a href="${base }/user/index.do" class="j-ajax"><span>用户授权</span></a></li>
			</ul>
		</div>
	</div>
	<div class="tabsContent" style="padding:0;" layoutH="0">
		<div>
			<jsp:include page="${base }/group/index.do"/>
		</div>
		<div></div>
	</div>
</div>