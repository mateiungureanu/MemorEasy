# Flashcards App – Java

## Metode de Dezvoltare Software (2024–2025)

## Table of Contents
- [Members](#members)
- [Backlog](#backlog-and-user-stories)
- [Diagrams](#diagrams)
- [Source Control](#source-control-with-git)
- [Automated Tests](#automated-tests)
- [Bug Reports](#bug-report-with-pull-request)
- [Design Patterns](#design-patterns)
- [Prompt Engineering](#prompt-engineering)

---

### Members
- Ungureanu Matei-Ștefan – [GitHub](https://github.com/mateiungureanu)
- Sarighioleanu Sebastian-Laurențiu – [GitHub](https://github.com/laur004)
- Telu Mihai-Sebastian – [GitHub](https://github.com/T-ms-code)

---

### Backlog and User Stories
Click **‘View done work items’** to see ALL user stories:  
🔗 https://sarighioleanulaurentiu.atlassian.net/jira/software/projects/MEMEASY/boards/34

---

### Diagrams
🔗 https://github.com/mateiungureanu/MemorEasy/tree/main/Diagrams

---

### Source Control with Git
🔗 GitHub Repository: https://github.com/mateiungureanu/MemorEasy

---

### Automated Tests
🔗 Unit and integration tests: https://github.com/mateiungureanu/MemorEasy/tree/main/src/test/java/com/unibuc/mds/memoreasy

---

### Bug Report with Pull Request
🔗 Merged pull requests:  
https://github.com/mateiungureanu/MemorEasy/pulls?q=is%3Apr+is%3Aclosed  
🔍 Related closed issues (bugs):  
https://github.com/mateiungureanu/MemorEasy/issues?q=is%3Aissue+is%3Aclosed

---

### Design Patterns
1) Builder - during the data export process, I implemented an approach inspired by the Builder design pattern, in which complex objects such as Category are constructed step by step, each including dependent entities (Chapter, Flashcard). This approach facilitates a clear organization of hierarchical data, which is essential for proper serialization into JSON format (https://github.com/mateiungureanu/MemorEasy/blob/main/src/main/java/com/unibuc/mds/memoreasy/Controllers/AllCategoriesController.java)
2) MVC (Model-View-Controller) – because our application architecture uses .fxml files, which define what the user sees (the View). Each .fxml file is associated with a corresponding Controller.java class, which handles the logic behind user interactions, such as button clicks. Additionally, both the .fxml file and its controller are typically linked to at least one Model class, which manages the underlying data and business logic.
3) Utility – because we have a Utils package that contains the utility classes DatabaseUtils, which manages the database connection, and ThemeManager, which handles switching between dark and light modes. Both classes contain only static methods and static final constants, as they are designed to be used without instantiation (https://github.com/mateiungureanu/MemorEasy/tree/main/src/main/java/com/unibuc/mds/memoreasy/Utils).
4) DTO (Data Transfer Object) – because we create simple objects (e.g., Category, Chapter) for import/export operations in JSON format. These objects contain only the relevant data needed for transfer, excluding unnecessary fields and without any business logic (https://github.com/mateiungureanu/MemorEasy/blob/main/src/main/java/com/unibuc/mds/memoreasy/Controllers/AllCategoriesController.java)

---

### Prompt Engineering
🔗 https://github.com/mateiungureanu/MemorEasy/blob/main/Prompt%20Engineering.docx

---

### Video Showcase
🔗 Youtube Link: https://www.youtube.com/watch?v=LfyjxMJr4Mo