function BlogControl(headingElement, postsElement) {

    this.all = function() {
        clear();
        $.get("/posts", function(data) {
            $.each(data, function(index, value){
                postsElement.append(render(value));
            })
            Prism.highlightAll();
        });
        header('All posts');
    };

    this.post = function(title) {
        clear();
        $.get("/post/" + title.replace(' ', '%20'), function(data) {
            postsElement.append(render(data));
            header(data.title);
            Prism.highlightAll();
        });
    };

    function clear() {
        postsElement.children().slice(1).remove();
    };

    function render(json) {
        var section = $("<section class='post'><header class='post-header'><header class='post-header'><h2 class='post-title' id='title'></h2><p class='post-meta'><div id='tags'></div></p></header><div class='post-description' id='content'></div></section>");

        var html = $(json.content);
        var title = json.title;
        var tags = json.tags;
        var added = json.added;

        section.find('#content').append(html);

        var tagsElement = section.find('#tags');
        for(var i in tags) {
            tagsElement.append($("<a class='post-category post-category-design' href='#'>" + tags[i] + "</a>"));
        }

        var titleElement = section.find('#title');
        titleElement.html(title);
        titleElement.attr('style', 'cursor: pointer');
        titleElement.click(function() {
            blogControl.post(title);
        });

        var createdAt = new Date(added);
        section.find('#added').html('' + createdAt.getFullYear() + '-' + (createdAt.getMonth() + 1) + '-' + createdAt.getDate());

        return section;
    };

    function header(text) {
        headingElement.text(text);
    };
};

var blogControl;

$(document).ready(function() {
    blogControl = new BlogControl($('#heading'), $('.posts'));
    blogControl.all();
});