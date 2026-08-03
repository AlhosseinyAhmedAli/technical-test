# Technical Test — API & Mobile Automation

Java automation solution covering:

- **Task 1** — API tests for `https://api.zippopotam.us/{country}/{postal-code}`
- **Task 2** — End-to-end mobile automation for the Wikipedia Android app
  ("save an article to a reading list")

Built with **Maven**, **TestNG**, **REST Assured**, and **Appium** (`java-client`).

## Project structure

```
technical-test/
├── pom.xml
├── README.md
└── src/test
    ├── java
    │   ├── api
    │   │   ├── base/BaseApiTest.java          # RestAssured setup shared by all API tests
    │   │   ├── models/                        # POJOs mapped 1:1 to the API's JSON response
    │   │   └── tests/ZippopotamApiTest.java    # All API test cases
    │   └── mobile
    │       ├── base/BaseMobileTest.java        # Appium driver lifecycle (setUp/tearDown)
    │       ├── utils/DriverFactory.java        # Builds the AndroidDriver from configurable capabilities
    │       ├── pages/                          # Page Object Model: SearchPage, ArticlePage, ReadingListsPage
    │       └── tests/SaveArticleToReadingListTest.java
    └── resources
        ├── schemas/location-response-schema.json  # JSON schema contract for the API
        ├── testng.xml           # default suite: API tests only
        └── testng-mobile.xml    # mobile suite: run explicitly (needs Appium + a device)

```
