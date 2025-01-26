<div style="text-align: center;"><span style="font-size: 40px"><b>fit-build-maven-plugin</b></span></div>

[TOC]

# Goals

## build-plugin

## build-app

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

(*) --> "Backup Original JAR"
--> "Add Original JAR Entries"
note right
<color:red><b>Collect Data</b></color>
1. <color:blue><b>Manifest</b></color> of original JAR
2. <color:blue><b>Main class</b></color> of original JAR
end note
--> "Add Launcher JAR Entries"
--> "Store Dependencies"
--> (*)
```

### MANIFEST.MF

> FIT-Class-Entry: xxx