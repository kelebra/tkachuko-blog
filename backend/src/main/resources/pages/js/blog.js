function BlogControl(headingElement, postsElement) {

    this.all = function() {
        clear();
        $.get("/posts", function(data) {
            $.each(data, function(index, value){
                // TODO: it has to be reworked after schema remodelling
                postsElement.append(preview(value));
            })
            Prism.highlightAll();
        });
        header('All posts');
    };

    this.post = function(id) {
        clear();
        $.get("/post/" + id, function(data) {
            postsElement.append(render(data));
            header(data.title);
            Prism.highlightAll();
        });
    };

    function clear() {
        postsElement.children().slice(1).remove();
    };

    function render(json) {
        var html = $(json.content);
        var id = json.id;
        var heading = html.find('h2').first();
        heading.attr('style', 'cursor: pointer');
        heading.click(function() {
            blogControl.post(id);
        });
        return html;
    };

    function preview(json) {
        var rendered = render(json);
        rendered.children('.post-description').remove();
        return rendered;
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