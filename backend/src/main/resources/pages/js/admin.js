// Specific to prism.js
function code() {
    var codeArea = $("#codeEditor");
    var selected = getSelection();
    var codeBlock = "<pre><code class = 'language-scala'>" + selected + "</code></pre>";

    var previousValue = codeArea.val();
    codeArea.val(previousValue.replace(selected, codeBlock));
}

function preview() {
    var template = "<!doctype html>    <html lang='en'>    <head>        <meta charset='utf-8'>        <meta name='viewport' content='width=device-width, initial-scale=1.0'>        <meta name='description' content='Yet Another Programming Blog'>            <title>Blog &ndash; Oleksii Tkachuk &ndash;</title>            <link rel='stylesheet' href='http://yui.yahooapis.com/pure/0.6.0/pure-min.css'>            <!--[if lte IE 8]>            <link rel='stylesheet' href='http://yui.yahooapis.com/pure/0.6.0/grids-responsive-old-ie-min.css'>            <![endif]-->        <!--[if gt IE 8]><!-->            <link rel='stylesheet' href='http://yui.yahooapis.com/pure/0.6.0/grids-responsive-min.css'>            <!--<![endif]-->                <!--[if lte IE 8]>        <link rel='stylesheet' href='/pages/css/blog-old-ie.css'>        <![endif]-->        <!--[if gt IE 8]><!-->        <link rel='stylesheet' href='/pages/css/blog.css'>        <!--<![endif]-->            <script src='http://code.jquery.com/jquery-2.2.1.min.js'></script>            <!-- Prism (code highlight) dependencies-->        <link href='/pages/css/syntax.css' rel='stylesheet'/>        <script src='/pages/js/syntax.js'></script>        </head>    <body>            <div id='layout' class='pure-g'>        <div class='sidebar pure-u-1 pure-u-md-1-4'>            <div class='header'>                <h1 class='brand-title'>Oleksii's blog</h1>                    <h2 class='brand-tagline'>Technical blog dedicated to java and scala</h2>                    <nav class='nav'>                    <ul class='nav-list'>                        <li class='nav-item'>                            <a class='pure-button' href='/'>Home</a>                        </li>                        <li class='nav-item'>                            <a class='pure-button' href='/blog'>Blog</a>                        </li>                    </ul>                </nav>            </div>        </div>            <div class='content pure-u-1 pure-u-md-3-4'>            <div>                <!-- A wrapper for all the blog posts -->                <div class='posts'>                    <h1 class='content-subhead'>All posts</h1>                                                #POST#                                                              </div>            </div>        </div>    </div>    </body>    </html>";
    var newWindow = window.open();
    newWindow.document.write(template.replace("#POST#", $("#codeEditor").val()));
}

// Return currently selected text
function getSelection() {
    var textComponent = document.getElementById('codeEditor');
    var selectedText;
    if (document.selection != undefined) {
        textComponent.focus();
        var sel = document.selection.createRange();
        selectedText = sel.text;
    } else if (textComponent.selectionStart != undefined) {
        var startPos = textComponent.selectionStart;
        var endPos = textComponent.selectionEnd;
        selectedText = textComponent.value.substring(startPos, endPos);
    }

    return selectedText;
}