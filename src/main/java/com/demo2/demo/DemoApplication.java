package com.demo2.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * spring boot使用两种方式与es交互
 * 1.jest
 * 2.spring data elasticsearch
 *      1)节点信息：clusterNodes;clusterName
 *      2)ElasticsearchTemplate
 *      3)ElasticseachRepositiry
 */

/**
 * {
 *   "name" : "AIS-23-PC",
 *   "cluster_name" : "elasticsearch",
 *   "cluster_uuid" : "_na_",
 *   "version" : {
 *     "number" : "7.5.0",
 *     "build_flavor" : "default",
 *     "build_type" : "zip",
 *     "build_hash" : "e9ccaed468e2fac2275a3761849cbee64b39519f",
 *     "build_date" : "2019-11-26T01:06:52.518245Z",
 *     "build_snapshot" : false,
 *     "lucene_version" : "8.3.0",
 *     "minimum_wire_compatibility_version" : "6.8.0",
 *     "minimum_index_compatibility_version" : "6.0.0-beta1"
 *   },
 *   "tagline" : "You Know, for Search"
 * }
 */
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
