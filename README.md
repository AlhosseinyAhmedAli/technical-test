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

**Design choices**

- **Page Object Model** for the mobile test keeps locators and interaction logic out of the
  test class, so the test method reads like the scenario in the brief and UI changes only
  require updating one page class.
- **POJOs + REST Assured `.as()`** for the API tests instead of raw JsonPath everywhere, so
  field-level assertions are typo-safe and refactor-friendly.
- **A JSON Schema contract test** guards against structural regressions (missing/renamed
  fields) independently of the value-based assertions.
- API and mobile tests are **separate TestNG suites** because they have very different
  runtime requirements (API tests need only network access; mobile tests need a running
  Appium server and a device/emulator with the app installed). `mvn test` runs only the API
  suite by default so CI doesn't fail for lacking a device.

---

## Task 1 — API tests

### What's covered

| # | Test | Why |
|---|------|-----|
| 1 | Valid request → `200` + `application/json` | Baseline contract |
| 2 | Response matches JSON schema | Structural regression guard |
| 3 | Field values correct for a known postcode (country, place, state) | Not just "200", but "correct" |
| 4 | Latitude/longitude are valid, in-range coordinates | Data-quality check |
| 5 | Data-driven happy path across US/GB/DE/CA | Endpoint isn't US-only |
| 6 | Non-existent (but well-formed) postal code → `404` | Error handling |
| 7 | Invalid country code → `404` | Error handling |
| 8 | Malformed postal code (letters) → `404` | Input validation |
| 9 | Missing postal code / empty country segment → non-200 | Boundary/edge case |
| 10 | Leading-zero postal code preserved (`00501`) | Regression guard against numeric coercion |
| 11 | Country code lookup is case-insensitive | Edge case specific to this API |
| 12 | `places[]` entries are fully populated | Array-level data integrity |
| 13 | Response time under budget | Basic non-functional check |

### Run it

```bash
cd technical-test
mvn test
```

This runs `src/test/resources/testng.xml`, i.e. `ZippopotamApiTest` only. No credentials or
extra setup needed — it hits the public `https://api.zippopotam.us` host directly.

Override the target host if needed (e.g. against a mock/staging instance):

```bash
mvn test -DbaseApiUri=https://api.zippopotam.us
```

---

## Task 2 — Mobile automation (Wikipedia app)

### Scenario automated

Search → open article → save → add to a brand-new reading list → navigate to Reading
Lists → search for the new list → verify the article is inside it. This maps directly to
steps 1–9 in the brief, implemented as:

```
SearchPage.openSearch()
          .searchFor("Artificial Intelligence")
          .openResult("Artificial intelligence")
   -> ArticlePage.saveArticle()
                 .openAddToReadingListDialog()
                 .createNewReadingList(listName)
   -> ReadingListsPage.navigateToReadingLists()
                       .searchForList(listName)
                       .openList(listName)
                       .isArticlePresent("Artificial intelligence")   // assertion
```

### Prerequisites to actually run this

1. **Android SDK + emulator** (or a physical device) with the Wikipedia app installed:
   `https://play.google.com/store/apps/details?id=org.wikipedia`
2. **Appium server** running locally: `npm install -g appium && appium`
   (with the `uiautomator2` driver: `appium driver install uiautomator2`)
3. Device/emulator visible via `adb devices`.

### Run it

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-mobile.xml \
         -DdeviceName="emulator-5554" \
         -DplatformVersion="14"
```

Or, to have Appium install the app fresh from a local `.apk` instead of assuming it's
already installed:

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-mobile.xml \
         -DappPath="/absolute/path/to/wikipedia.apk"
```

All capabilities (`appiumUrl`, `deviceName`, `platformVersion`, `appPackage`,
`appActivity`, `appPath`) are read from system properties in `DriverFactory`, so the same
code runs against a local emulator, CI device farm, or a cloud grid (BrowserStack/Sauce
Labs) — only the launch command changes.

### A note on locators

The resource-ids in the `mobile.pages` classes match the Wikipedia Android app's public
build at the time of writing. App UI ids can change between releases; if a locator breaks,
recapture it with **Appium Inspector** and update the corresponding field in
`SearchPage` / `ArticlePage` / `ReadingListsPage` — the test class itself doesn't need to
change, which is the point of the Page Object Model. I did not have access to a running
emulator/Appium server in the environment this was written in, so the mobile suite is
structurally complete and ready to run but hasn't been executed against a live device —
worth a first run + a locator pass with Appium Inspector before relying on it.

---

## Getting this onto GitHub

```bash
git init
git add .
git commit -m "Add API and mobile automation for technical test"
git branch -M main
git remote add origin <your-empty-github-repo-url>
git push -u origin main
```
