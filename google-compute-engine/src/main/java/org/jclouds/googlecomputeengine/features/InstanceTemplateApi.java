/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.googlecomputeengine.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import static org.jclouds.googlecomputeengine.domain.Instance.SerialPortOutput;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseCallerArg0ToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Path("/global/instanceTemplates")
@Consumes(APPLICATION_JSON)
public interface InstanceTemplateApi {

   /** Returns an instanceTemplate by name or null if not found. */
   @Named("Instances:get")
   @GET
   @Path("/{instanceTemplate}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   InstanceTemplate get(@PathParam("instanceTemplate") String instanceTemplate);

   /**
    * Creates a instanceTemplate resource in the specified project using the data included in the request.
    *
    * @param template the instance template
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:insert")
   @POST
   @Produces(APPLICATION_JSON)
   Operation create(@BinderParam(BindToJsonPayload.class) NewInstance template);

   /** Deletes an instanceTemplate by name and returns the operation in progress, or null if not found. */
   @Named("Instances:delete")
   @DELETE
   @Path("/{instance}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("instance") String instance);

   /**
    * Retrieves the list of instance resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Instances:list")
   @GET
   ListPage<Instance> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("Instances:list")
   @GET
   @Transform(InstancePages.class)
   Iterator<ListPage<Instance>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("Instances:list")
   @GET
   @Transform(InstancePages.class)
   Iterator<ListPage<Instance>> list(ListOptions options);

   static final class InstancePages extends BaseCallerArg0ToIteratorOfListPage<Instance, InstancePages> {

      private final GoogleComputeEngineApi api;

      @Inject InstancePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Instance>> fetchNextPage(final String zoneName, final ListOptions options) {
         return new Function<String, ListPage<Instance>>() {
            @Override public ListPage<Instance> apply(String pageToken) {
               return api.instancesInZone(zoneName).listPage(pageToken, options);
            }
         };
      }
   }

}

