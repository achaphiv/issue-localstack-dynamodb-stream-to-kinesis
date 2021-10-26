package bug;

import java.net.URI;
import java.util.Map;
import java.util.Random;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

public class WriteToDynamodb {
  public static void main(String[] args) {
    Random random = new Random();
    try (
      DynamoDbClient client = DynamoDbClient.builder()
        .endpointOverride(URI.create("http://localhost:4566"))
        .build()
    ) {
      String hash = "hash-" + random.nextInt(1000);
      String range = "range-" + random.nextInt(1000);
      client.putItem(
        PutItemRequest.builder()
          .tableName("bug")
          .item(
            Map.of(
              "hash",
              AttributeValue.builder().s(hash).build(),
              "range",
              AttributeValue.builder().s(range).build()
            )
          )
          .build()
      );
      client.deleteItem(
        DeleteItemRequest.builder()
          .tableName("bug")
          .key(
            Map.of(
              "hash",
              AttributeValue.builder().s(hash).build(),
              "range",
              AttributeValue.builder().s(range).build()
            )
          )
          .build()
      );
      System.out.println(hash);
      System.out.println(range);
    }
  }
}
