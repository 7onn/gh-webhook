FROM clojure:openjdk-8-lein as builder
WORKDIR /app
COPY . ./
RUN lein uberjar

FROM java:8-alpine
RUN addgroup -S app && \
  adduser -S app -G app
USER app
WORKDIR /app
COPY --chown=app:app --from=builder /app/target/webhook-0.0.1.jar .
CMD java -jar webhook-0.0.1.jar
