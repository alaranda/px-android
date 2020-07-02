# PX :: Checkout Mobile Payments

# Change Log

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
