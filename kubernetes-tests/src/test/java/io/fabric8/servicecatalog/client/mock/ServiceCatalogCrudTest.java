/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.servicecatalog.client.mock;

import io.fabric8.servicecatalog.api.model.ClusterServiceBroker;
import io.fabric8.servicecatalog.api.model.ClusterServiceBrokerBuilder;
import io.fabric8.servicecatalog.api.model.ClusterServiceBrokerList;
import io.fabric8.servicecatalog.client.ServiceCatalogClient;
import io.fabric8.servicecatalog.server.mock.ServiceCatalogServer;

import org.junit.Rule;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Class testing crud operations on ServiceCatalog
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableRuleMigrationSupport
public class ServiceCatalogCrudTest {

  @Rule
  public ServiceCatalogServer server = new ServiceCatalogServer(true, true);

  /**
   * Creates two brokers and gets list of all brokers.
   * List mustn't be null and have size of 2.
   */
  @Test
  @Order(1)
  public void testList() {
    ServiceCatalogClient client = server.getServiceCatalogClient();

    ClusterServiceBroker broker1 = new ClusterServiceBrokerBuilder()
      .withNewMetadata()
      .withName("broker1")
      .endMetadata()
      .withNewSpec()
      .withUrl("https://broker1.example.com")
      .endSpec()
      .build();

    ClusterServiceBroker broker2 = new ClusterServiceBrokerBuilder()
      .withNewMetadata()
      .withName("broker2")
      .endMetadata()
      .withNewSpec()
      .withUrl("https://broker2.example.com")
      .endSpec()
      .build();

    client.clusterServiceBrokers().create(broker1);
    client.clusterServiceBrokers().create(broker2);

    ClusterServiceBrokerList brokers = client.clusterServiceBrokers().list();

    assertNotNull(brokers);
    assertEquals(2, brokers.getItems().size());
  }

  /**
   * Method creates broker and attempts to find it by name to determine if create was successful.
   * If created successfully search won't return null.
   */
  @Test
  @Order(2)
  public void testGet() {
    ServiceCatalogClient client = server.getServiceCatalogClient();

    ClusterServiceBroker brokerMock = new ClusterServiceBrokerBuilder()
      .withNewMetadata()
      .withName("broker")
      .endMetadata()
      .withNewSpec()
      .withUrl("https://broker.example.com")
      .endSpec()
      .build();

    client.clusterServiceBrokers().create(brokerMock);

    ClusterServiceBroker brokerGet = client.clusterServiceBrokers().withName("broker").get();

    assertNotNull(brokerGet);
  }

  /**
   * Method creates broker from test-broker.yml file.
   * If load was successful search for it should not return null.
   */
  @Test
  @Order(3)
  public void testLoadFromFile() {
    ServiceCatalogClient client = server.getServiceCatalogClient();


    ClusterServiceBroker brokerFromFile = client.clusterServiceBrokers().load(getClass().getResourceAsStream("/test-broker.yml")).get();

    client.clusterServiceBrokers().create(brokerFromFile);


    ClusterServiceBroker brokerGet = client.clusterServiceBrokers().withName("broker").get();

    assertNotNull(brokerGet);
  }

  /**
   * Method is creating broker and attempting to find one with that exact name and deletes it.
   * If operation is successful second search for broker with that name should return null.
   */
  @Test
  @Order(4)
  public void testDelete() {
    ServiceCatalogClient client = server.getServiceCatalogClient();

    ClusterServiceBroker broker = new ClusterServiceBrokerBuilder()
      .withNewMetadata()
      .withName("broker")
      .endMetadata()
      .withNewSpec()
      .withUrl("https://broker.example.com")
      .endSpec()
      .build();

    client.clusterServiceBrokers().create(broker);

    assertTrue(client.clusterServiceBrokers().withName("broker").delete());
    assertNull(client.clusterServiceBrokers().withName("broker").get());
  }

}