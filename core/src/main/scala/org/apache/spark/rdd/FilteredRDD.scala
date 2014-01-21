/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.rdd

import org.apache.spark.{OneToOneDependency, Partition, TaskContext}

/* 将当前RDD作为第一个参数，函数作为第二个参数，返回值是filter后的RDD。*/
private[spark] class FilteredRDD[T: ClassManifest](
    prev: RDD[T],
    f: T => Boolean)
  extends RDD[T](prev) {

  // firstParent会从deps中取出第一个RDD对象, 就是传入的prev RDD, 
  // 在One-to-one Dependency中,parent和child的partition信息相同
  override def getPartitions: Array[Partition] = firstParent[T].partitions

  override val partitioner = prev.partitioner    // Since filter cannot change a partition's keys

  // compute是真正产生RDD的函数
  override def compute(split: Partition, context: TaskContext) =
    firstParent[T].iterator(split, context).filter(f)
}
