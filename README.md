# systematic
## Overview
A new, unique back-end for the [StArt](https://www.lapes.ufscar.br/resources/tools-1/start-1) systematic study tool. Helps researchers easily create, develop and organize their systematic studies.
Currently planned to work as a web app ([systematic-front](https://github.com/pet-ads/systematic-front)).

## How to install and run
Systematic is being developed using the **Kotlin** programming language. To run the project, you will need an IDE (ex: [IntelliJ](https://www.jetbrains.com/idea/)).
Then, you may download or clone the application and open the project there. It was created using JDK 17, which can be downloaded externally or through the IDE.

Following that, you will need to run the [Docker Engine](https://www.docker.com/products/docker-desktop/). It has to be running on your computer for the program to function.
Then, you must open a terminal on the folder the project is on (or click on the terminal button in your IDE), and run the following command to initialize the container:

`docker compose up -d --build`

The above command must be used every time you want to use the project.

Now, all that's left is to open the `web` module > `src` > `main` > `kotlin` > `br.all` > right-click on `WebApplication.kt` and click on **Run 'WebApplication.main()'** 

There! Now the back-end is up and running and can process HTTP requests.

## Requests
The project is currently set to run on the address `http://localhost:8080`. To view all possible requests, as documented on the application, open the following page on your browser:
http://localhost:8080/swagger-ui.html

The documentation contains the necessary parameters and all possible return codes and values.

## Modules
Systematic is currently composed of the following modules:

- `review`: the main module. Contains all working domain rules, entities and persistence code for the program.
- `account`: encompasses everything related to the user account system.
- `web`: provides the necessary elements of a RESTful API, allowing the connection of a web app.

## Tests
To run the currently implemented tests go to the module you want to test > `src` > `test`. Open that folder and right-click on the `kotlin` folder. 
Then, click on **Run 'Tests in 'kotlin''**.

For the `web` module, you must have the Docker Engine open, and the container running.

## License
This project is developed under the [GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.pt-br.html) license.

## Development Team
Developed with :heart: by [PET/ADS @IFSP São Carlos](http://petads.paginas.scl.ifsp.edu.br/).
