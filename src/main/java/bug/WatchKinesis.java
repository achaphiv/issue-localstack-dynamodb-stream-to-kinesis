package bug;

import java.nio.ByteBuffer;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import com.amazonaws.services.kinesis.model.Record;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;

public class WatchKinesis {
  public static void main(String[] args) {
    worker().run();
  }

  private static Worker worker() {
    KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(
      "watch-kinesis",
      "localstack",
      DefaultAWSCredentialsProviderChain.getInstance(),
      "worker"
    );
    config.withMaxRecords(1000);
    config.withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);
    config.withMetricsLevel(MetricsLevel.NONE);

    EndpointConfiguration endpoint = new EndpointConfiguration(
      "http://localhost:4566",
      "us-east-1"
    );

    return new Worker.Builder()
      .config(config)
      .kinesisClient(
        AmazonKinesisClient.builder()
          .withEndpointConfiguration(endpoint)
          .build()
      )
      .dynamoDBClient(
        AmazonDynamoDBClient.builder()
          .withEndpointConfiguration(endpoint)
          .build()
      )
      .cloudWatchClient(
        AmazonCloudWatchClient.builder()
          .withEndpointConfiguration(endpoint)
          .build()
      )
      .recordProcessorFactory(new KinesisRecordProcessor.Factory())
      .build();
  }

  static final class KinesisRecordProcessor implements IRecordProcessor {
    public static final class Factory implements IRecordProcessorFactory {
      @Override
      public IRecordProcessor createProcessor() {
        return new KinesisRecordProcessor();
      }
    }

    @Override
    public void initialize(InitializationInput input) {}

    @Override
    public void processRecords(ProcessRecordsInput input) {
      for (Record record : input.getRecords()) {
        ByteBuffer data = record.getData();
        try {
          JsonNode deserialized = new ObjectMapper().readTree(data.array());
           System.err.println("Record: " + deserialized);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }

    @Override
    public void shutdown(ShutdownInput input) {
    }
  }
}
