# PX :: Checkout Mobile Payments

# Change Log

## [1.26.0]

### Added

- Se agrega whitelist de collectors nuevos sniffeados desde COW para setear flowid correspondiente.

## [1.25.0]

### Added

- Se agrega al paymentBody los campos conceptId, conceptAmount, sponsorId, statementDescriptor, paymentExpirationDate,
purpose, description y purposeDescriptor.

## [1.24.0]

### Added

- Se agrega llamada a PreferenceApi en la congrats.
- Se agrega a la respuesta de congrats el primary button y el auto return.

## [1.23.0]

### Added

- Se agrega UserAgent de la lib de dto en el context.
- Se borra el UserAgent de la toolkit del RemedyRequest.
- Se deja de devolver Consumer Credits como remedy para versiones de IOS anteriores a 4.36.4.

## [1.22.0]

### Added

- Se agrega el operatorId en la info del collector que va a payments

### Changed

- Se actualiza la lib de dto a la version 0.20.0

## [1.21.0]

### Added

- Se actualiza la version de px-toolkit a 0.41.1
- Agrego version param para las imagenes de ODR.

## [1.20.0]

### Added

- Se empieza a recibir la merchant order id en el request y se envia a payments, sino viene sigue la logica vieja.

### Changed

- Se cambia a Map el tipo del campo internal_metadata de la pref.

## [1.19.1]

### Changed

- Se saca credits del ofrecimiento de remedies.


## [1.19.0]

### Changed

- Se agrega configuracion de melidata.
- Se agrega logica de experimentos en remedies(sin ningun experimento activo).

## [1.18.3]

### Changed

- Cuando es Rejectado Accoun Money o Consumer Credits se muestra un titulo generico.

## [1.18.2]

### Changed

- Se corrije bug cuando no hay medios alternativos.

## [1.18.1]

### Changed

- Se corrije el payment method rejected type de credits.

## [1.18.0]

### Added

- Se agrega nueva logica de ordenamiento para silver bullet.
Si rechazamos TD / AM:
    AM
    CC en 1 Cuota
    TC con ESC en 1 cuota
    TD con ESC en 1 cuota
    TD sin ESC en 1 cuota
    TC sin ESC en 1 cuota

Si rechazamos TC / CC:
    CC en 1 cuota
    TC con ESC en 1 cuota
    AM
    TD con ESC en 1 cuota
    TC sin ESC en 1 cuota
    TD sin ESC en 1 cuota

## [1.17.0]

- Se borra la clase ifpe utils y se deja activado ifpe.

## [1.16.2]

### Changed

- Fix header de seguridad en payments.

## [1.16.1]

### Added

- Se agrega validacion de marketplaceFee mayor a 0.

## [1.16.0]

### Added

- Se agrega el campo applicationFee y taxes en el post a payments.

### Changed

- Se mapea el title que devuelve descuentos y si tiene contenido se usa ese, si esta vacio usamos el anterior.

## [1.15.0]

### Changed

- Se habilitan product ids para division de gastos.

## [1.14.1]

### Changed

- envType local en build.gradle.

## [1.14.0]

### Changed

- Se actualiza las librerias de docker a java-mini

## [1.13.2]

### Changed

- Cuando el status detail es cc_rejected_call_for_authorize se ofrece Silver bullet.

## [1.13.1]

### Changed

- Se utiliza el discount id como campaign id para realizar pago.

## [1.13.0]

### Added

- Se agrega silver bullet para cc_rejected_bad_filled_date.
- Se agrega silver bullet para rejected_by_regulations.
- Se agrega silver bullet para rejected_by_bank.
- Se agrega silver bullet para rejected_bank_error.
- Se agrega silver bullet para cc_rejected_card_disabled.

### Changed

- Se usa silver bullet como default para cualquier apicall con error o validacion.
- Se modifican todas las metricas de remedies.

## [1.12.0]

### Added

- Agrego header "x-forwarded-for" en la llamada a payments.

## [1.11.0]

### Added

- Se agrega a la respuesta de congrats un mapa con imagenes de odr para los medios de pago.

## [1.10.1]

### Changed

- Vuelvo lo implementation a compile en el build.gradle.

## [1.10.0]

### Added

- Se agrega a la respuesta el flowId y el productId.
- Se agrega en el request a payments internal metadata y external reference.

### Changed

- Refactor en Init Preference
- Se invierten el hint message y el title en el remedy de cvv.
- Saco api de users y la reemplazo por kyc para el flujo de pago de facturas de meli.
- Actualizacion de la lib de api 0.3.0.
- Actualizacion de la lib de dto 0.9.0.

## [1.9.0]

### Added

- Se comienza a enviar el User-Agent de las apps en la pegada a Merch Engine.
- Se excluye MLU del flujo de división de gastos con amigos.

## [1.8.1]

### Changed

- Se cambia el color del texto para expense split

## [1.8.0]

### Added

- Se agrega header de security indicando el uso de 2fa o no, en request a payments

## [1.7.0]

### Added

- Agrego validacion para no devolver remedy de CVV para versiones de Android 4.48.X.

## [1.6.1]

### Changed

- Se modifica la url de división de gastos con amigos, se usa la misma
tanto para ML como MP

## [1.6.0]

### Added

- Se agrega la funcionalidad de división de gastos con amigos

## [1.5.0]

### Added

- Se Agrega un flag en la respuesta de congrats que
habilita o deshabilita el customOrder para los product ids de instore

## [1.4.6]

### Changed

- Se actualiza el modelo de Discounts.

## [1.4.5]

### Changed

- Actualizo keys de babel.

## [1.4.4]

### Changed

- Cambio en metricas para Sivler Bullet.

## [1.4.3]

### Added

- Se agrega a la firma un nuevo nodo con informacion para tracking del front.

## [1.4.2]

### Changed

- Modifico metricas y agrego el flow a los logs.

## [1.4.1]

### Changed

- Agrego chequeo de medios de pagonalternativos para remedy silver bullet en versiones viejas que no lo mandan.

## [1.4.0]

### Added

- Se agrega remedy Silver Bullet y Call for Authorize.

## [1.3.4]

### Added

- Se agrega métrica para trackear request al endpoint /congrats, se agregan tags en métricas de endpoint de preferencias.

## [1.3.3]

### Changed

- Se cambia el host de la api de Risk.

## [1.3.2]

### Changed

- Se agrega validacion para cuando no viene el security code location.

## [1.3.1]

### Added

- Se agrega IfpeUtils.

### Changed

- Se modifica validacion para mostrar nodo viewReceipt.

## [1.3.0]

### Added

- Se agrega java code formatter

## [1.2.1]

### Changed

- Se modifica el link a kyc y se reemplaza mercadolibre por meli en el link.

## [1.2.0]

### Added

- Se agrega nuevo parametro de entrada ifpe.
- Se agrega en la respuesta el nodo view_receipt con link a activities.
- Se agrega en el caso que sea ifpe un mensaje en la respuesta del tipo Text.

## [1.1.5]

### Changed

- Se agrega el nodo del boton a la respuesta de high risk, traducciones de babel y formateo del path (esc_cap y remedies).

## [1.1.4]

### Changed

- Todos los remedies sin deesarrollo son seteadoscomo remedies genericos.

## [1.1.3]

### Changed

- Se dejan de usar remedies no mapeados.

## [1.1.2]

### Changed

- Se arregla log de request y se actualiza readme
- Se actualiza versión de babel

## [1.1.1]

### Changed

- Se agregan las traducciones con babel y lombok.

## [1.1.0]

### Added

- Se agrega nuevo endpoint de remedies.

## [1.0.3]

### Changed

- Se mejoraron logs y se cambió validación que daba 500 en lugar de 400.

## [1.0.2]

### Changed

- Se cambiaron el tipo del campo id en el DTO User de Long a String y en PaymentBody el collector paso de ser Long a User

## [1.0.1]

### Changed

- Se arregla nombre de parámetros que habían sido renombrados collectorId y payerId

## [1.0.0]

### Added

- Integración con Babel (traducciones)

### Changed
- Actualización de lib de DTOs a versión 0.1.6 y toolkit 0.33.0.

## [0.71.1]

### Changed

- Se deja de recibir el x-clinet-id y toma el client id del access token para hacer el reset.

## [0.71.0]

### Changed

- Agrego endpoint que resetea el cap del esc.

## [0.70.0]

### Changed

- Se cambia el nombre del nodo express por el de onetap y se setea en true.

## [0.69.3]

### Changed

- Cuando el platform es MP se le sugiera descargar ML y cuando es ML se le sugiere descargar MP.
- Cambio parametro de pegada a la api de users para obtener el email.

## [0.69.1]

### Changed

- Se saca validacion de headers que genera errores en el RestUtils.

## [0.68.0]

### Changed

- Se deja en español el lenguaje default.

## [0.67.2]

### Changed

- Se vuelve a setear el link de loyalty para las versiones iugal o mayores a PX/iOS/4.24.3 en la congrats.

## [0.67.0]

### Removed

- Se eliminó el parseo de internal_meta_data al generar el pago


## [0.66.0]

### Changed

- Para los user agent menores a PX/iOS/4.26 en la congrats, en el titulo del link de loyalty se setea "".

## [0.65.0]

### Changed

- Para los user agent menores a PX/iOS/4.24.3 en la congrats, en el titulo del link de loyalty se setea "".

## [0.64.0]

### Changed

- Para los User Agent menores a PX/Android/4.23.1 no se hace la llamada a loyalty.

## [0.63.0]

### Changed

- Cuando el platform es "OTHER", se arma  link de loyalty con el prefijo meli.

## [0.62.0]

### Changed

- Se pasa a minuscula el header x-client-name en la pegada a loyalty y se agrega el platform a la url.

## [0.61.0]

### Changed

- Para los user agent menores o igual a PX/iOS/4.24.2 en la congrats, en el titulo del link de loyalty se setea "".

## [0.60.0]

### Changed

- Para los user agent menores o igual a PX/iOS/4.24.2 en la congrats, en el link de loyalty se setea "".
- Se cambian el titulo y se saca el subtitulo de la seccion de descuentos en la congrats.

## [0.58.0]

### Changed

- Para el user agent PX/iOS/4.22 no se devuelve puntos.

## [0.57.0]

### Added

- Changelog.

### Changed

- Actualizacion readme.
