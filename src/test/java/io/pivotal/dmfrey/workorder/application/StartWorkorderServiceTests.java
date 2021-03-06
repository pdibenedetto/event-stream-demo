package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.TimestampGenerator;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import io.pivotal.dmfrey.workorder.application.in.StartWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import io.pivotal.dmfrey.workorder.domain.events.NameUpdated;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderOpened;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderStarted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class StartWorkorderServiceTests {

    private StartWorkorderService subject;

    private GetWorkorderEventsPort mockGetWorkorderEventsPort;
    private PersistWorkorderEventPort mockPersistWorkorderEventPort;
    private NodeValidatorQuery mockNodeValidatorQuery;
    private TimestampGenerator mockTimestampGenerator;

    private UUID fakeWorkorderId = UUID.randomUUID();
    private String fakeTitle = "fakeTitle";
    private String fakeUser = "testUser";
    private String fakeNode = "fakeNode";
    private ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

    @BeforeEach
    public void setup() {

        this.mockGetWorkorderEventsPort = mock( GetWorkorderEventsPort.class );
        this.mockPersistWorkorderEventPort = mock( PersistWorkorderEventPort.class );
        this.mockNodeValidatorQuery = mock( NodeValidatorQuery.class );
        this.mockTimestampGenerator = mock( TimestampGenerator.class );

        this.subject = new StartWorkorderService( this.mockGetWorkorderEventsPort, this.mockPersistWorkorderEventPort, this.mockNodeValidatorQuery, this.mockTimestampGenerator );

        when( this.mockNodeValidatorQuery.execute( any(NodeValidatorQuery.ValidateNodeCommand.class ) ) ).thenReturn( fakeNode );
        when( this.mockTimestampGenerator.generate() ).thenReturn( fakeOccurredOn );

    }

    @Test
    public void testExecute() {

        NameUpdated fakeNameUpdated = new NameUpdated( fakeWorkorderId, fakeTitle, fakeUser, fakeNode, fakeOccurredOn );
        WorkorderOpened fakeWorkorderOpened = new WorkorderOpened( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn );
        when( this.mockGetWorkorderEventsPort.getWorkorderEvents( fakeWorkorderId ) ).thenReturn( asList( fakeNameUpdated, fakeWorkorderOpened ) );

        this.subject.execute( new StartWorkorderUseCase.StartWorkorderCommand( fakeWorkorderId ) );

        List<WorkorderDomainEvent> expectedEvents = new ArrayList<>();
        expectedEvents.add( new WorkorderStarted( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn ) );

        verify( this.mockGetWorkorderEventsPort ).getWorkorderEvents( fakeWorkorderId );
        verify( this.mockPersistWorkorderEventPort ).save( fakeWorkorderId, expectedEvents );
        verify( this.mockTimestampGenerator ).generate();
        verifyNoMoreInteractions( this.mockGetWorkorderEventsPort, this.mockPersistWorkorderEventPort, this.mockTimestampGenerator );

    }

}
