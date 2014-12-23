function renderTopNav(site){
    var navbar = $('#templates #top-navbar').html()
    $('#top').html(navbar)

    if(site){
        //render site-specific top navigation
        $.ajax({
            type: "GET",
            url: "/api/sites/" + site
        }).done(function(msg){
            var site = msg['sites'][0]
            $('#top #site-name').html(site.name)
        })

        //render right side
        var rightSideNav = $(document.createElement('ul'))
        rightSideNav.attr('class', 'nav navbar-nav navbar-right')

        //generate the add endpoint link
        var addEndpointLink = $(document.createElement('a'))
        addEndpointLink.click(function(){
            renderCreateEndpoint(site);
        })
        addEndpointLink.append("Add Endpoint")

        var addEndpointListItem = $(document.createElement('li'))
        addEndpointListItem.html(addEndpointLink)

        //add the endpoint link onto the navbar
        rightSideNav.append(addEndpointListItem)

        //add the rightside onto the navbar
        $('#top #top-container').append(rightSideNav)
    }


    //generate site-dropdown
    $.ajax({
        type: "GET",
        url: "/api/sites"
    }).done(function(msg){

        //generate list elements for each site, add them to dropdown
        $(msg['sites']).each(function(i, site){
            console.log(site);

            var link = $(document.createElement('a'));
            link.html(site.name);
            link.attr('href', '?site=' + site.id);

            var item = $(document.createElement('li'));
            item.html(link)

            $('#top #site-dropdown').append(item)
        })

        var addSiteLink = $(document.createElement('a'))
        addSiteLink.append("Add Site")
        addSiteLink.click(function(){
            renderCreateSite();
        })
        var addSiteListItem =


        $('#top #site-dropdown').append(addSiteLink);

    }).fail(function(msg){
        console.log("top navbar render site dropdown failed")
        console.log(msg);
    });

}

function renderNav(){
    $.ajax({
        type: "GET",
        url: "/api/topics",
    }).success(function(res){
        console.log("rendering nav");

        //generate the navlist element
        var navlist = $(document.createElement('div'))

        //generate the topic list
        $(res['topics']).each(function(i, topic){
            //generate subtopics sublist
            var subTopicList = $(document.createElement('ul'))
            subTopicList.attr('class', 'nav nav-pills nav-stacked')

            $(topic.subTopics).each(function(i, subTopic){
                var name = subTopic.subTopic;
                var id = subTopic.id;

                var link = $(document.createElement('a'));
                link.click(function(){
                    renderRead(id);
                })
                link.css('padding', '0px');
                link.append(name);

                var subTopicItem = $(document.createElement('li'));
                subTopicItem.attr('role', 'presentation');
                subTopicItem.append(link)

                subTopicList.append(subTopicItem)
            });


            //add topic elem
            navlist.append($(document.createElement('h3')).append(topic.topic));
            navlist.append(subTopicList);
        });

        //append navlist to DOM
        $('#navigation').html(navlist)
    });
}
