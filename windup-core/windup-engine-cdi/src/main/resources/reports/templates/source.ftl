<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>example-app.war - Application Report</title>
    <link href="../../resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="../../resources/css/windup.css" rel="stylesheet" media="screen">
	<link rel='stylesheet' type='text/css' href='${relativePath}snippet/jquery.snippet.min.css' />
	<link rel='stylesheet' type='text/css' href='${relativePath}windup.css' />
	<link rel='stylesheet' type='text/css' href='${relativePath}sausage/sausage.css' />
  </head>
  <body role="document">
    
    <!-- Fixed navbar -->
    <div class="navbar-fixed-top windup-bar" role="navigation">
      <div class="container theme-showcase" role="main">
        <img src="../../resources/img/windup-logo.png" class="logo"/>
      </div>
    </div>



    <div class="container" role="main">
        <div class="row">
          <div class="page-header page-header-no-border">
            <h1>Application Report <span class="slash">/</span><small style="margin-left: 20px; font-weight: 100;">${application.applicationName}</small></h1>
             <div class="navbar navbar-default">
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-responsive-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
            </div>
            <div class="navbar-collapse collapse navbar-responsive-collapse">
              <ul class="nav navbar-nav">
                <li><a href="index.html">Application</a></li>
                <li><a href="ejbs.html">EJBs</a></li>
                <li><a href="hibernate.html">Hibernate</a></li>
                <li><a href="spring.html">Spring</a></li>
                <li><a href="server-resources.html">Server Resources</a></li>
              </ul>
            </div><!-- /.nav-collapse -->
            </div>
          </div>
        </div>
    </div>



    <div class="container theme-showcase" role="main">

	<pre id='source'><#t><#rt>
		${source.sourceBody?html}<#t><#rt>
	</pre><#t><#rt>
	
    </div> <!-- /container -->

    <script src="https://code.jquery.com/jquery.js"></script>
    <script src="../../resources/js/bootstrap.min.js"></script>
    
	<script type='text/javascript' src='${relativePath}jquery-ui/jquery.ui.widget.js'></script>
	<script type='text/javascript' src='${relativePath}snippet/jquery.snippet.min.js'></script>
	<script type='text/javascript' src='${relativePath}snippet/jquery.snippet.java-manifest.js'></script>
	<script type='text/javascript' src='${relativePath}sausage/jquery.sausage.min.js'></script>

    
	    
	<script type='text/javascript'>
		$(document).ready(function(){
			$('pre').snippet('${snippet}',{style:'ide-eclipse', showNum:true,boxFill:'#ffeeb9', box: '${blockSetting}' });
	
	
		<#list lineNumberSuggestions as dr>
			<#assign lineNumber = dr.lineNumber>
			$("<div id='${lineNumber?c}-inlines' class='inline-source-hint-group'><#t><#t><#rt>
			").appendTo('ol.snippet-num li:nth-child(${lineNumber?c})');
		</#list>
		
		<#list lineNumberSuggestions as dr>
			<#assign lineNumber = dr.lineNumber>
			
			$("<a name='${dr.hashCode()?c}'></a><#t>
				<div class='inline-source-comment green'><#rt><#rt>
					<#if dr.description?has_content>
						<div class='inline-comment'><div class='inline-comment-heading'><h2 class='notification ${dr.level?lower_case}'>${dr.description?js_string}</h2></div><#t><#rt>
							<#if dr.hints??>
								<#list dr.hints as hint>
										<div class='inline-comment-body'>${hint?j_string}</div><#t><#rt>
								</#list>
							</#if>
						</div><#t><#rt>
					</#if>
				</div><#t><#rt>
			").appendTo('#${lineNumber?c}-inlines');<#t><#rt>
		</#list>
		
		
			$('code[data-code-syntax]').each(function(){
		         var codeSyntax = ($(this).data('code-syntax'));
		         if(codeSyntax) {
		            $(this).parent().snippet(codeSyntax,{style:'ide-eclipse', menu:false, showNum:false});
		         }
			});
			$(window).sausage({ page: 'li.box' });
		}); 
	</script>
    
  </body>
</html>