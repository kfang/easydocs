function renderUpdate(id){

    $.ajax({
        type: "GET",
        url: "/api/endpoints/" + id,
    }).success(function(res){

        //add the form
        var template = $('#templates #create-form').html()
        $('#content').html(template)

        //add the title
        $('#content #form-title').html("Update Endpoint:");

        //bind pre-filled information
        var endpoint = res['endpoints'][0]

        $('#content input[name="topic"]').val(endpoint.topic);
        $('#content input[name="subTopic"]').val(endpoint.subTopic);

        $('#content input[name="route"]').val(endpoint.route);
        $('#content select[name="method"]').val(endpoint.method);
        $('#content input[name="contentType"]').val(endpoint.contentType);

        if(endpoint.authentication){
            $('#content select[name="authentication"]').val(endpoint.authentication);
        }

        if(endpoint.parameters){
            $('#content textarea[name="parameters"]').val(endpoint.parameters);
        }

        if(endpoint.notes){
            $('#content textarea[name="notes"]').val(endpoint.notes);
        }

        //bind the submit button
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
                type: "PUT",
                url: "/api/endpoints/" + id,
                data: JSON.stringify(data),
                contentType: "application/json"
            }).success(function(msg){
                setTimeout(function(){
                    renderNav();
                    renderRead(id);
                }, 500);
            }).error(function(msg){
                alert(msg.responseText);
            })

            return false;
        })
    }).error(function(res){
        alert(res.responseText);
    })
}