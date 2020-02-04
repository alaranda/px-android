# PX :: Checkout Mobile Payments

# Change Log

## [0.69.2]

### Changed

- Cuando el platform es MP se le sugiera descargar ML y cuando es ML se le sugiere descargar MP.

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
