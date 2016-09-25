<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>elFinder 2.0</title>

    <!-- jQuery and jQuery UI (REQUIRED) -->
    <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/themes/smoothness/jquery-ui.css">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>

    <!-- elFinder CSS (REQUIRED) -->
    <link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/elfinder.min.css" />">
    <link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/theme.css" />">

    <!-- elFinder JS (REQUIRED) -->
    <script type="text/javascript" src="<c:url value="/resources/js/elfinder.min.js" />"></script>

    <!-- elFinder translation (OPTIONAL) -->
    <!-- 
    <script type="text/javascript" src="<c:url value="/resources/js/i18n/elfinder.pt_BR.js" />"></script>
     -->

    <!-- elFinder initialization (REQUIRED) -->
    <script type="text/javascript" charset="utf-8">
		// Documentation for client options:
		// https://github.com/Studio-42/elFinder/wiki/Client-configuration-options
		$(document).ready(function() {
			$('#elfinder').elfinder({
				url : 'elfinder/connector',  // connector URL (REQUIRED)
				//lang: 'pt_BR'                    // language (OPTIONAL)
			});
		});
    </script>
</head>
<body>
	<!-- Element where elFinder will be created (REQUIRED) -->
	<div id="elfinder"></div>
	<div>
		<h2>Volume: Root (Hard Disk): can new/rename/delete/download file and folder</h2>
		<h2>Volume: Google Driver: This one is a cloud Volumn (Google Driver API), for now only can explore a folder and download file; other features still not implement </h2>
	</div>
</body>
</html>
