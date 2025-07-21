# 📦 Batch d'import des Companies et Employees

Ce projet Spring Boot 3.5.3 utilise **Spring Batch** pour lire un fichier JSON contenant des entreprises (`companies`) et leurs employés (`employees`), et les insérer dans une base de données relationnelle (SQLite en production, H2 en test).

---

## 🚀 Fonctionnalités

- 📄 Lecture d’un fichier JSON de type :

```json
{
  "companies": [
    {
      "id": "compA123",
      "name": "TechNova",
      "location": "Paris, France",
      "industry": "Software",
      "employees": [
        {
          "id": "emp001X",
          "name": "Alice Dupont",
          "position": "Software Engineer",
          "email": "alice.dupont@technova.com"
        },
        ...
      ]
    },
    ...
  ]
}
```
---

## Lancer le batch manuellement

Le batch importe les données contenues dans un fichier JSON et les insère dans les tables `COMPANIES` et `EMPLOYEES`.

### Configuration du chemin du fichier JSON

Le chemin du fichier JSON est configurable via la propriété :

```properties
batch.files.paths.input.json=./data/jsons/companies.json
```

