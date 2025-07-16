Write-Host "Building CRM project"

.\gradlew "-Dorg.gradle.java.home=$Env:JAVA_HOME" bootJar

#docker-compose up --build