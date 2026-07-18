build:
	docker build -t my-java-test .

run:
	docker run --rm -v $$(pwd)/allure-results:/app/target/allure-results my-java-test

report:
	allure generate allure-results --clean -o allure-report
	allure open allure-report
