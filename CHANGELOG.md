# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.5.0] - 2019-11-18
### Added
- Команда `clearweightprovider` для отключения активной системы веса
- PlayerWeightTracker из мода [ConfigurableWeightSystem](https://github.com/RareScrap/ConfigurableWeight) стал стандартным механизмом апи для отслеживания изменения в инвентарях. Отныне моддерам более не нужно опредеять свой трекер инвентаря и обеспечивать его совместимость с другими системами веса.

### Fixed
- Личный вывод команд `getweightproviders` и `getactiveweightprovider` теперь не отправляется другим администраторам

## [0.4.0] - 2019-11-14
### Added
- Демострационные системы веса доступны в виде запускаемых сабпроджектов. Это упрощает тестирование как самого WeightAPI, так и совместимости вашего мода с другими системами веса. Примеры оступны на Bintray, что позволяет разработчику подключать их через мезанизм зависимостей Gradle.
- `examples/case1` - Пример самостоятельной системы веса
- `examples/case2` - Пример системы веса, расширящей уже существующую система [Configurable Weight System](https://github.com/RareScrap/ConfigurableWeight)
- При запуске WeightAPI из IDE примеры автоматически загружаются как отдельные моды.
- Добавлен метод WeightRegistry#applyToClient(), упрощающий регистрацию системы веса на клиенте без потребности в регистрации.

## [0.3.0] - 2019-06-14
### Added
- Добавлена команда `getweightproviders`, отображающая все зарегистрированные на сервере системы веса.
- Добавлена команда `getactiveweightprovider`, отображающая активную систему веса сервера.
- Добавлена команда `setweightprovider`, задающая новую систему веса.
- При остановке сервера текующая система веса сохраняется и восстанавливается при следующем запуске.
- Механизм синхронизации систем веса с клинтом теперь встроен в WeightAPI
- Добавлен евент, выбрасывающийся при изменении активной системы веса `WeightProviderChangedEvent`