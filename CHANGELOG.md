# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.0] - 2019-09-24
### Added
- Демострационные системы веса доступны в виде сабпроджектов. Это упрощает тестирование WeightAPI и взаимодействия между модами.
- `examples/case1` - Пример самостоятельной системы веса
- `examples/case2` - Пример системы веса, расширящей уже существующую система [Configurable Weight System](https://github.com/RareScrap/ConfigurableWeight)
- Добавлен метод WeightRegistry#applyToClient(), упрощающи регистрацию системы веса на клиенте без потребности в регистрации.

## [0.3.0] - 2019-06-14
### Added
- Добавлена команда `getweightproviders`, отображающая все зарегистрированные на сервере системы веса.
- Добавлена команда `getactiveweightprovider`, отображающая активную систему веса сервера.
- Добавлена команда `setweightprovider`, задающая новую систему веса.
- При остановке сервера текующая система веса сохраняется и восстанавливается при следующем запуске.
- Механизм синхронизации систем веса с клинтом теперь встроен в WeightAPI
- Добавлен евент, выбрасывающийся при изменении активной системы веса `WeightProviderChangedEvent`