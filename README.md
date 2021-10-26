# Issue

- Create kinesis stream
- Create dynamodb table
- Connect dynamodb table to kinesis stream
- Write to dynamodb table
- See data on kinesis stream
- Notice kinesis stream has null `OldImage`

## How

Create kinesis and dynamodb tables and point the table to the kinesis stream:

```bash
docker-compose down -v
docker-compose up -d
```

Start watching the kinesis stream

```bash
./mvnw -P watch-kinesis
```

In another terminal, write/delete an item:

```bash
./mvnw -P write-to-dynamodb
```
