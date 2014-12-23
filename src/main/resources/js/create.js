
function renderCreateEndpoint(site){

    var createForm = $('#templates #create-form').html()
    $('#content').html(createForm)

    $('#content #form-submit').click(function(){

        var data = {};

        data['site'] = site;

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


function renderCreateSite(){
    var createSiteForm = $('#templates #create-site-form').html()
    $('#content').html(createSiteForm)

    $('#content #create-site-submit').click(function(){
        var data = {}

        data['name'] = $('#content input[name="name"]').val()

        $.ajax({
            type: "POST",
            url: "/api/sites",
            data: JSON.stringify(data),
            contentType: "application/json"
        }).success(function(msg){
            setTimeout(function(){
                window.location.replace("/web/index.html?site=" + msg['sites'][0].id)
            })
        }).error(function(msg){
            alert(msg.responseText);
        })

        return false;
    })
}
