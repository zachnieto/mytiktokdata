package com.webapp.mytiktokdata;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

/**
 * Class for Connecting to blob and uploading images.
 */
public abstract class AbstractImageProcess {


  /**
   * Uploads the given file to the Blob Container.
   *
   * @param filePath file to be uploaded
   */
  protected void upload(String filePath, String username) {

    String connectionStr = "DefaultEndpointsProtocol=https;AccountName=mytiktokdatastorage;AccountKey=ox8pULwbi4NSOUD2WXtKqxi5P6SlIl9uO4c6mqL5yJGCYa2xaBMATDhovQg4hNM5QSpnVsN298W5zV4jIj8hnw==;EndpointSuffix=core.windows.net";

    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionStr).buildClient();

    BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("data");

    BlobClient blobClient = blobContainerClient.getBlobClient(username + "_" + filePath);

    blobClient.uploadFromFile(filePath, true);
  }

}
