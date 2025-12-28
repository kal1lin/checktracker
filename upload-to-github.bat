@echo off
REM GitHub upload script for CheckTracker
REM Сценарий для загрузки проекта на GitHub

setlocal enabledelayedexpansion

cls
echo ========================================
echo CheckTracker - GitHub Upload Script
echo ========================================
echo.

REM Проверка Git
git --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Git не установлен!
    echo Установите Git с https://git-scm.com
    pause
    exit /b 1
)

echo [1/5] Проверяю git статус...
git status >nul 2>&1
if errorlevel 1 (
    echo [INFO] Инициализирую новый git репозиторий...
    git init
    git config user.name "CheckTracker"
    git config user.email "dev@checktracker.local"
)

echo [2/5] Добавляю все файлы...
git add .
if errorlevel 1 (
    echo [ERROR] Ошибка при добавлении файлов
    pause
    exit /b 1
)

echo [3/5] Создаю первый commit...
git commit -m "Initial commit: CheckTracker v1.0.0" || (
    echo [INFO] Файлы уже в репозитории, пропускаю commit
)

echo.
echo ========================================
echo ИНСТРУКЦИЯ ДЛЯ ЗАГРУЗКИ НА GITHUB
echo ========================================
echo.
echo 1. Перейдите на https://github.com/new
echo.
echo 2. Создайте НОВЫЙ репозиторий с названием: checktracker
echo.
echo 3. Выберите опции:
echo    - Description: "Mobile expense tracker with receipt OCR"
echo    - Public (для доступа к Actions)
echo    - НЕ создавайте README, .gitignore (они уже есть)
echo.
echo 4. После создания GitHub покажет команды.
echo    Скопируйте строку с "git remote add origin"
echo.
echo 5. Вставьте эту команду ниже и нажмите Enter:
echo.
echo Пример команды:
echo git remote add origin https://github.com/YOUR_USERNAME/checktracker.git
echo.
set /p REMOTE_CMD="Введите команду (или нажмите Enter если уже добавлен remote): "

if not "!REMOTE_CMD!"=="" (
    echo [4/5] Добавляю remote...
    !REMOTE_CMD!
    if errorlevel 1 (
        echo [WARNING] Возможно remote уже добавлен, продолжаю...
    )
)

echo [4/5] Переименовываю ветку в main...
git branch -M main

echo [5/5] Загружаю на GitHub (может занять 1-2 минуты)...
git push -u origin main

if errorlevel 1 (
    echo.
    echo [WARNING] Ошибка при загрузке!
    echo.
    echo Вероятные причины:
    echo 1. Неправильный URL репозитория
    echo 2. Нет доступа в интернет
    echo 3. SSH ключ не настроен
    echo.
    echo Решение: используйте HTTPS с токеном доступа
    echo https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo [SUCCESS] Проект загружен на GitHub!
echo ========================================
echo.
echo Следующие шаги:
echo.
echo 1. Перейдите на: https://github.com/YOUR_USERNAME/checktracker/actions
echo.
echo 2. Там будут автоматически запущены workflows:
echo    - Build iOS IPA
echo    - Build Android APK
echo    - Run Unit Tests
echo.
echo 3. Ждёте 20-30 минут пока сборка завершится
echo.
echo 4. Скачиваете готовые файлы:
echo    - CheckTracker-iOS (IPA файл)
echo    - CheckTracker-Android (APK файлы)
echo    - test-reports (результаты тестов)
echo.
echo [!] Для работы Actions нужно 2000 минут/месяц бесплатно
echo.
pause
