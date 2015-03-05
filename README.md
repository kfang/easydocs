##Http Endpoints
###Sites
- **GET**  /api/sites
- **POST** /api/sites
    - ex: {"name":"my-site-name"}
- **GET** /api/sites/:id
###Site Topics
- **GET /api/sites/:id/topics
###Endpoint
- **GET** /api/endpoints/:id
- **POST** /api/endpoints
    - authentication is optional
    - parameters is optional
    ```json
    {
        "site":"my-site-id",
        "topic":"route-topic",
        "subTopic":"route-subtopic",
        "notes":"stuff to know about the route (supports markdown)",
        "route":"/the/api/route",
        "method":"GET",
        "contentType":"application/json",
        "authentication":"required",
        "parameters":"stuff about the request params or body (supports markdown)"
    }
    ```
- **PUT** /api/endpoints/:id
    - its the same as creating a promotion except you can't change `site`
- **DELETE** /api/endpoints/:id