#compile

javac -cp "postgresql-42.2.8.jar" -d bin (Get-ChildItem -Path GUI/bobaapp -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

#run

java -cp "bin;postgresql-42.2.8.jar" bobaapp.Main