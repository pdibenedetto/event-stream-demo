package io.pivotal.dmfrey.workorder.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@JsonPropertyOrder({ "eventType", "workorderId", "user", "node", "occurredOn" })
public class WorkorderCompleted extends AbstractWorkOrderDomainEvent {

    @JsonCreator
    public WorkorderCompleted(
            @JsonProperty( "workorderId" ) final UUID workorderId,
            @JsonProperty( "user" ) final String user,
            @JsonProperty( "node" ) final String node,
            @JsonProperty( "occurredOn" ) final ZonedDateTime occurredOn
    ) {
        super( workorderId, user, node, occurredOn );

    }

    @Override
    @JsonIgnore
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
