Near Real-time Search for Public Twitter Stream
==============================================

Main class: `NRTSearch`

### Setup
In order to run `NRTSearch`, you must save your Twitter API OAuth credentials in a file named `twitter4j.properties` in your current working directory. See [this page](http://twitter4j.org/en/configuration.html) for more information about Twitter4j configurations. The file should contain the following (replace the `**********` instances with your information):
```
oauth.consumerKey=**********
oauth.consumerSecret=**********
oauth.accessToken=**********
oauth.accessTokenSecret=**********
```

### Usage
```
 -dir <arg>   Directory path for the index
 -ui <arg>    UI Type (console or web)
```
If `console` is chosen, please query via the console. If `web` is chosen, please query via a Web browser (http://localhost:8080/search?query=yourQueryWords).
