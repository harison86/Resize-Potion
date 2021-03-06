package com.camellias.resizer.network.packets;

import com.camellias.resizer.Main;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAlteredSize extends PacketOnResize
{
	public boolean isGrowth;
	public int duration, amplifier;
	
	public PacketAlteredSize() {}
	
	public PacketAlteredSize(EntityLivingBase entity, boolean isGrowth, int duration, int amplifier, boolean shouldSpawnParticles)
	{
		super(entity, shouldSpawnParticles);
		this.isGrowth = isGrowth;
		this.duration = duration;
		this.amplifier = amplifier;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);
		buf.writeBoolean(isGrowth);
		buf.writeInt(duration);
		buf.writeInt(amplifier);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);
		isGrowth = buf.readBoolean();
		duration = buf.readInt();
		amplifier = buf.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketAlteredSize, IMessage>
	{
		@Override
		public IMessage onMessage(PacketAlteredSize message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
			{
				EntityLivingBase entity = message.removePotionEffect(ctx, !message.isGrowth, message.isGrowth);
				if (entity != null)
				{
					entity.addPotionEffect(new PotionEffect(message.isGrowth ? Main.GROWTH : Main.SHRINKING, message.duration, message.amplifier));
				}
			});
			
			return null;
		}
	}
}
