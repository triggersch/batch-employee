# 📦 Batch d'import des Companies et Employees

Ce projet Spring Boot 3.5.3 utilise **Spring Batch** pour lire un fichier **flat file COBOL** (`.dat`) contenant des entreprises (`C` records) et leurs employés (`E` records), puis les insérer dans une base de données relationnelle (SQLite en production, H2 en test).

---

## 🚀 Fonctionnalités

- 📄 Lecture d’un fichier séquentiel plat, avec des enregistrements fixes :
  - **Company** (type `C`) : 

```dat
C + id(10) + name(30) + location(30) + industry(20)
``` 
  - **Employee** (type `E`) : 

```dat
E + id(10) + name(30) + position(25) + email(40) + companyId(10)
```
- Exemple de contenu du fichier `companies.dat` :

    ```
    CcompA123  TechNova                      Paris, France                 Software            
    Eemp001X   Alice Dupont                  Software Engineer      alice.dupont@technova.com       compA123  
    Eemp002Y   Marc Lemoine                  Product Manager        marc.lemoine@technova.com       compA123  
    CcompB456  GreenLogix                    Lyon, France                  Logistics          
    Eemp003Z   Claire Martin                 Logistics Coordinator claire.martin@greenlogix.com     compB456  
    Eemp004W   Youssef Belkacem              Operations Analyst    youssef.belkacem@greenlogix.com  compB456  
    ```
---

## Lancer le batch manuellement

Le batch importe les données contenues dans un fichier plat (.dat) et les insère dans les tables `COMPANIES` et `EMPLOYEES`.

### Configuration du chemin du fichier JSON

Le chemin du fichier JSON est configurable via la propriété :

```properties
batch.files.paths.input.dat=./data/flat/companies.dat
```

