
function renderCreate(){

    var createForm = $('#templates #create-form').html()
    $('#content').html(createForm)

    $('#content #form-submit').click(function(){

        var data = {};

        $('#content input').each(function(i, e){
            var elem = $(e)
            data[elem.attr('name')] = elem.val()
        })

        $('#content textarea').each(function(i, e){
            var elem = $(e)
            data[elem.attr('name')] = elem.val()
        })

        $('#content select').each(function(i, e){
            var elem = $(e)
            data[elem.attr('name')] = elem.val()
        })

        $.ajax({
            type: "POST",
            url: "/api/endpoints",
            data: JSON.stringify(data),
            contentType: "application/json"
        }).success(function(msg){
            setTimeout(function(){
                renderNav()
            }, 1000);
        }).error(function(msg){
            alert(msg.responseText);
        })


        return false;
    })
}
