package com.jayway.demo.library.rest.resources.hateoas;

import com.jayway.jaxrs.hateoas.Linkable;
import com.jayway.jaxrs.hateoas.core.HateoasResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 4/27/12
 * Time: 8:38 PM
 */
@Path("/item")
public class ItemResource {

    @GET()
    @Path("/search/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Linkable(LinkableIds.SEARCH_ITEM_ID)
    public Response getItems(@PathParam("keyword") String keyword
            , @QueryParam("noLinks") boolean noLinks
    ) {

        final Collection<ItemDto> entitiesColl = fromBeanCollection();

        return (HateoasResponse
                .ok(entitiesColl)
                        //.link(LinkableIds.ITEM_UPDATE_ID, Rels.ITEMS_REL)
                .selfLink(LinkableIds.SEARCH_ITEM_ID, keyword)
                .each(LinkableIds.BOOK_DETAILS_ID, "BOOK_DETAILS_ID", "id")
                .each(LinkableIds.CUSTOMER_DETAILS_ID, "CUSTOMER_DETAILS_ID", "sellerName")
                .each(LinkableIds.CUSTOMER_LOANS_ID, "CUSTOMER_LOANS_ID", "category"))
                .build();


        /*
             return (noLinks
                 ? Response.ok(entitiesColl)
                 : HateoasResponse
                     .ok(entitiesColl)
                     //.link(LinkableIds.ITEM_UPDATE_ID, Rels.ITEMS_REL)
                     .selfLink(LinkableIds.SEARCH_ITEM_ID, keyword)
                     .each(LinkableIds.BOOK_DETAILS_ID, "BOOK_DETAILS_ID", "id")
                     .each(LinkableIds.CUSTOMER_DETAILS_ID, "CUSTOMER_DETAILS_ID", "sellerName")
                     .each(LinkableIds.CUSTOMER_LOANS_ID, "CUSTOMER_LOANS_ID", "category"))
                 .build();
                 */
    }

    private Collection<ItemDto> fromBeanCollection() {

        return Arrays.asList(
                new ItemDto("name.0", "id.0", "condition.0", "listingType.0", "itemUrl.0", 0D, "sellerName.0", "category.0"),
                new ItemDto("name.1", "id.1", "condition.1", "listingType.1", "itemUrl.1", 1D, "sellerName.1", "category.1")
        );
    }


}
