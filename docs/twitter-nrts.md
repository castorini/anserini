# Twitter (Near) Real-Time Search with Anserini

To get access to the Twitter public stream, you need a developer account to obtain OAuth credentials. After creating an 
account on the Twitter developer site, you can obtain these credentials by [creating an "application"](https://dev.twitter.com/apps/new). 
After you've created an application, create an access token by clicking on the button "Create my access token".

To to run the Twitter (near) real-time search demo, you must save your Twitter API OAuth credentials in a file named 
`twitter4j.properties` in your current working directory. See [this page](http://twitter4j.org/en/configuration.html) for 
more information about Twitter4j configurations. The file should contain the following (replace the `**********` instances 
with your information):

```
oauth.consumerKey=**********
oauth.consumerSecret=**********
oauth.accessToken=**********
oauth.accessTokenSecret=**********
```

Once you've done that, fire up the demo with:

```
sh target/appassembler/bin/TweetSearcher -index twitter-index
```

The demo starts up an HTTP server on port `8080`, but this can be changed with the `-port` option. Query via a web browser 
at `http://localhost:8080/search?query=query`. Try `birthday`, as there are always birthdays being celebrated.

User could change the maximum number of hits returned at 'http://localhost:8080/search?query=birthday&top=15'. The default 
number of hits is 20. 
