FROM maven:3.8.3-openjdk-17

ENV PROJECT_HOME /usr/src/todosimpleapp
ENV JAR_NAME todosimpleapp.jar

RUN mkdir -p $PROJECT_HOME
WORKDIR $PROJECT_HOME

COPY . .

RUN mvn clean package -DskipTests

RUN mv $PROJECT_HOME/target/$JAR_NAME $PROJECT_HOME

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod","todosimpleapp.jar"]