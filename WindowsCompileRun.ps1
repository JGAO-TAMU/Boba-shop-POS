#compile

javac -cp "GUI/postgresql-42.2.8.jar" -d GUI/bin (Get-ChildItem -Path GUI/bobaapp -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

#run

java -cp "GUI/bin;postgresql-42.2.8.jar" bobaapp.Main