# PX :: Checkout Mobile Payments

![language java8](https://img.shields.io/badge/language-java8-blue.svg?style=flat) ![technology Gradle](https://img.shields.io/badge/technology-Gradle-blue.svg?style=flat)
[![kibana](https://img.shields.io/badge/-Kibana-%23f058c3.svg?style=flat&logo=kibana)](http://furyshort4.logs.furycloud.io/app/kibana#/dashboard/fury-px-checkout-mobile-payments_dashboard)
[![datadog](https://img.shields.io/badge/-Datadog-%23672edf.svg?style=flat)](https://app.datadoghq.com/dashboard/x87-vjp-749/px-checkout-mobile-payments?from_ts=1571155273617&live=true&tile_size=s)
[![new_relic](https://img.shields.io/badge/-New%20Relic-6ebbce.svg?style=flat)](https://rpm.newrelic.com/accounts/989586/applications/312653384)
[![fury](https://img.shields.io/badge/-Fury-6ECE80.svg?style=flat)](http://fury.ml.com/#/px-checkout-mobile-payments/general)

## [Changelog](./CHANGELOG.md)

#### Add environment variables

* `MLAUTH_AUTH_ACCESS_TOKEN_URI=https://internal-api.mercadolibre.com/auth/access_token/`

## Translations :: Babel

### Gettex Installation:
```
brew install gettext
brew link gettext --force
```

### How to generate the jar file with the translations of Babel

```
make babel-translations
```

* How to see the status of translations:

```
./gradlew i18n-status
```

* How to get all the messages used in the project:

```
./gradlew i18n-gettext
```

* Upload (when you added or updated a message)

```
./gradlew i18n-upload
```

* Download - To download translated messages

```
./gradlew i18n-download
```

* Makemo - To compile messages and package them

```
./gradlew i18n-makemo
```

## Documentation :: Swagger

* Update via [APIDefinition Class](/src/doc/java/com/mercadolibre/swagger/APIDefinition.java) and execute the command:
    * `"./gradlew swagger"`

* Update on Fury
    * `fury -> documentation -> promote`

## Endpoints

| Method Type   | Endpoint                    |
| ------------- |:---------------------------:|
| POST          | /px_mobile/legacy_payments |
| GET          | /px_mobile/congrats   |   
| DELETE          | /px_mobile/esc_cap   |   
| POST          | /px_mobile/remedies   |

## Questions

* [px_nativo@mercadolibre.com](px_nativo@mercadolibre.com)