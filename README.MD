# my-java-test

Instructions for building the Docker container, running tests, and generating the Allure report.

## Requirements

- Docker
- Make
- Allure CLI (for generating and opening the report locally)

## Command overview (Makefile)

| Command        | Description                                                      |
|----------------|------------------------------------------------------------------|
| `make add_env` | create .env file from template                                   |
| `make build`   | Builds the `my-java-test` Docker image                           |
| `make run`     | Runs the container and executes the tests, saving Allure results |
| `make report`  | Generates the Allure HTML report and opens it in a browser       |

## 1. Build the container

```bash
make build
```

Builds the `my-java-test` image from the `Dockerfile` in the current directory.

## 2. Run the tests

```bash
make run
```

Runs the `my-java-test` container and mounts the local `allure-results` folder into the container (`/app/target/allure-results`). This ensures test results (Allure `.json`/`.xml` files) are saved on the host after the container exits, even with the `--rm` flag.

## 3. Generate and view the report

```bash
make report
```

Generates an HTML report from the `allure-results` folder into `allure-report` (clearing the previous version via the `--clean` flag) and opens it in the browser using Allure's built-in server.

## Full workflow

To build the image, run the tests, and view the report in sequence:

```bash
make build
make run
make report
```

## Notes

- The `allure-results` folder is created automatically in the project root the first time `make run` is executed.
- To clear old results before a new test run, remove the folder manually:
  ```bash
  rm -rf allure-results allure-report
  ```
- `allure open` starts a local web server — keep the terminal open while viewing the report.