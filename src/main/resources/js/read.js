function renderRead(id){

    $.ajax({
        type: "GET",
        url: "/api/endpoints/" + id,
    }).success(function(res){
        var converter = new Showdown.converter();
        var template = $('#templates #read-endpoint').html()
        $('#content').html(template)

        var endpoint = res['endpoints'][0]

        $('#content #topic').append(endpoint.topic);
        $('#content #subTopic').append(endpoint.subTopic);

        $('#content #route').append(endpoint.route);
        $('#content #method').append(endpoint.method);
        $('#content #contentType').append(endpoint.contentType);

        if(endpoint.authentication){
            $('#content #authentication').append(endpoint.authentication);
        }

        if(endpoint.parameters){
            var rendered = converter.makeHtml(endpoint.parameters);
            $('#content #parameters').append($(rendered));
        }

        if(endpoint.notes){
            var rendered = converter.makeHtml(endpoint.notes);
            $('#content #notes').append($(rendered));
        }

        //generate the update button
        var updateButton = $(document.createElement("button"))
        updateButton.attr('class', "btn btn-primary")
        updateButton.append("Update")
        updateButton.click(function(){
            renderUpdate(id);
        })
        $('#content').append(updateButton);

        //generate the delete button
        var deleteButton = $(document.createElement("button"))
        deleteButton.attr('class', "btn btn-warning")
        deleteButton.css('margin-left', '10px');
        deleteButton.append("Delete")
        deleteButton.click(function(){
            renderDelete(id);
        })
        $('#content').append(deleteButton);

    }).error(function(res){
        alert(res.responseText);
    })
}