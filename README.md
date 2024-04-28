# AnhyRepo Plugin Documentation

## Overview

The AnhyRepo plugin is designed to create and manage "repositories" — personal storage spaces for commands or text messages in Minecraft. These repositories can be used for quickly inserting text into the command line or chat, with the option for prior editing.

### Essential Dependencies for [**AnhyRepo**](https://dev.anh.ink/AnhyRepo/)
The primary requirement for **AnhyRepo** to function is the installation of the [**AnhyLibAPI**](https://dev.anh.ink/anhylibapi/) library version 1.5.2 or higher. Without **AnhyLibAPI**, **AnhyRepo** will not operate.

Additionally, **AnhyRepo** is compatible with [**AnhyLingo**](https://dev.anh.ink/anhylingo/) version 0.3.3 or higher. Installing **AnhyLingo** expands the multilingual capabilities of **AnhyRepo**. However, its absence does not affect the primary operation of **AnhyRepo**.

## Features

- **Repository Creation:** Users can create up to 9 different repositories.
- **Record Management:** Each repository can contain up to 27 entries.
- **User Interface:** Repository management menu implemented through a custom inventory.

## Commands

The full version of the command: `/anhyrepo args...`, aliases: `/repo args...` or the shortest version `/ar args...`

- `/ar gui <i>`: Opens the repository menu. If <i> — the number of a repository is specified, it opens that particular repository.
- `/ar new <repo_name>` or `/ar n <repo_name>`: Creates a new repository.
- `/ar regroup <repo_index> <new_name>` or `/ar rg <repo_index> <new_name>`: Renames a repository.
- `/ar add <repo_index> <name>/<text>` or `/ar a <repo_index> <name>/<text>`: Adds a new entry. The name and text are entered separated by a slash "/".
- `/ar rename <repo_index> <item_index> <new_name>` or `/ar rn <repo_index> <item_index> <new_name>`: Renames an entry.
- `/ar retext <repo_index> <item_index> <new_text>` or `/ar rt <repo_index> <item_index> <new_text>`: Changes the text in an entry.
- `/ar reitem <repo_index> <item_index>` or `/ar ri <repo_index> <item_index>`: Changes the icon of an entry to the type of item held in hand.
- `/ar remove <repo_index> <item_index>`: Removes an entry.

## Usage

1. **Opening the Menu:** Enter the command `/repo gui` to access the main repository menu or `/repo gui <i>` to access a specific repository.
2. **Adding an Entry:** Enter the command `/repo add <repo_index> <name>/<text>`, where `<name>` is the short name of the entry, and `<text>` is the text or command to be stored.
3. **Editing and Deleting:** Use the commands `/repo rename`, `/repo retext`, and `/repo remove` to manage existing entries.

## Interface

- **Inventory Clicks:** A left-click on an item in the inventory inserts the necessary text into the command line. Use a right-click to return to the main menu.
- **Visualization:** Icons for entries vary: a command block for commands, a sheet of paper for short messages, and a book with a quill for long texts.

## Use Cases

The AnhyRepo plugin can be useful in various scenarios:
- **For server administrators:** Convenient storage and quick entry of repetitive commands, such as managing rights or server configurations.
- **For content creators:** The ability to quickly invoke commands for scripted events or manage NPCs (e.g., in Denizen scripts).
- **For players:** Storing frequently used text messages or commands, conveniently switching between different sets of commands depending on the server situation.
