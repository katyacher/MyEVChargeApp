<h2> Инструкция по сборке apk-файла </h2>
<h4>Через командную строку:</h4>
Откройте терминал в папке проекта (или перейдите в нее через cd)  

Выполните команду для сборки:  
bash  
(Linux/Ubuntu)./gradlew assembleRelease  
(Windows) gradlew.bat assembleRelease  

Если нужно подписать APK, сначала создайте файл keystore.properties в папке проекта:  
storePassword=ваш_пароль  
keyPassword=ваш_пароль  
keyAlias=ваш_алиас  
storeFile=путь/к/файлу.jks  

Добавьте в app/build.gradle перед android {:  

gradle  
def keystoreProperties = new Properties()  
def keystorePropertiesFile = rootProject.file('keystore.properties')  
if (keystorePropertiesFile.exists()) {  
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))  
}  

android {  
    signingConfigs {  
        release {  
            storeFile file(keystoreProperties['storeFile'])  
            storePassword keystoreProperties['storePassword']  
            keyAlias keystoreProperties['keyAlias']  
            keyPassword keystoreProperties['keyPassword']  
        }  
    }  
    buildTypes {  
        release {  
            signingConfig signingConfigs.release  
            minifyEnabled false  
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'  
        }  
    }  
}
Снова выполните:
bash
./gradlew assembleRelease
Готовый APK будет в: app/build/outputs/apk/release/app-release.apk
