# RC Translate - Grupo 25

Projeto de Redes de Computadores 2016/2017 - "Tradução RC" - Técnico Lisboa

### Compilação do projeto

O projeto requer uma versão do Java 1.8 (o grupo usou a 1.8.0_102)

```sh
$ make
```

### Passos de execução

Executar o TCS:
```sh
$ java -cp build/ rc.translate.g25.TCS [-p TCSport]
```

Executar o TRS:
```sh
$ java -cp build/ rc.translate.g25.TRS language [-p TRSport] [-n TCSname] [-e TCSport]
```

Executar o User:
```sh
$ java -cp build/ rc.translate.g25.User [-n TCSname] [-p TCSport]
```
