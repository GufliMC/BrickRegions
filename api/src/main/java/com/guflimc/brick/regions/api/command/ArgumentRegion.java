package com.guflimc.brick.regions.api.command;

import com.guflimc.brick.regions.api.data.Region;
import com.guflimc.brick.regions.api.data.RegionType;
import com.guflimc.brick.regions.api.RegionAPI;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArgumentRegion extends Argument<Region> {

    public static final int REGION_NOT_EXIST = 1;

    private RegionType regionType = null;

    public ArgumentRegion(@NotNull String id) {
        super(id);
    }

    public ArgumentRegion withRegionType(RegionType type) {
        this.regionType = type;
        return this;
    }

    @Override
    public @NotNull Region parse(@NotNull String input) throws ArgumentSyntaxException {
        Optional<Region> region;
        if ( regionType == null ) {
            region = RegionAPI.get().region(input);
        } else {
            region = RegionAPI.get().region(input).filter(rg -> rg.type() == regionType);
        }

        return region.orElseThrow(() ->
                new ArgumentSyntaxException("Region does not exist.", input, REGION_NOT_EXIST));
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        List<Region> regions = new ArrayList<>(RegionAPI.get().regions());
        if ( regionType != null ) {
            regions = regions.stream().filter(rg -> rg.type() == regionType).collect(Collectors.toList());
        }

        // Create a primitive array for mapping
        DeclareCommandsPacket.Node[] nodes = new DeclareCommandsPacket.Node[regions.size()];

        // Create a node for each restrictions as literal
        for (int i = 0; i < nodes.length; i++) {
            DeclareCommandsPacket.Node argumentNode = new DeclareCommandsPacket.Node();

            argumentNode.flags = DeclareCommandsPacket.getFlag(DeclareCommandsPacket.NodeType.LITERAL,
                    executable, false, false);
            argumentNode.name = regions.get(i).name();
            nodes[i] = argumentNode;
        }
        nodeMaker.addNodes(nodes);
    }
}
