package example.examplemod.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

/**
 * Annihilator Turret Entity - Stationary energy cannon with rapid-fire capabilities
 * Based on SuperbWarfare's GunShootGoal rapid-fire system
 */
class AnnihilatorTurretEntity(type: EntityType<out AnnihilatorTurretEntity>, world: Level) : Entity(type, world) {
    
    // Firing state
    var isFiring: Boolean = false
    
    // Energy/Ammo
    private var energy by ENERGY
    private var maxEnergy by MAX_ENERGY
    
    // Rapid-fire timing
    private var shootTimer: Long = 0
    private var lastFireTime: Long = 0
    
    // Targeting
    private var targetX by TARGET_X
    private var targetY by TARGET_Y
    private var targetZ by TARGET_Z
    
    // Configuration (from SuperbWarfare's RPM system)
    var fireRate: Double = 10.0  // Rounds per second (like RPM / 60)
    var spreadAmount: Float = 2.0f  // Spread in degrees
    
    init {
        this.noPhysics = true
    }
    
    override fun defineSynchedData() {
        this.entityData.define(ENERGY, 0)
        this.entityData.define(MAX_ENERGY, 100)
        this.entityData.define(TARGET_X, 0.0)
        this.entityData.define(TARGET_Y, 0.0)
        this.entityData.define(TARGET_Z, 0.0)
    }
    
    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("Energy", this.energy)
        compound.putInt("MaxEnergy", this.maxEnergy)
        compound.putDouble("TargetX", this.targetX)
        compound.putDouble("TargetY", this.targetY)
        compound.putDouble("TargetZ", this.targetZ)
        compound.putDouble("FireRate", this.fireRate)
        compound.putFloat("Spread", this.spreadAmount)
    }
    
    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("Energy")) this.energy = compound.getInt("Energy")
        if (compound.contains("MaxEnergy")) this.maxEnergy = compound.getInt("MaxEnergy")
        if (compound.contains("TargetX")) this.targetX = compound.getDouble("TargetX")
        if (compound.contains("TargetY")) this.targetY = compound.getDouble("TargetY")
        if (compound.contains("TargetZ")) this.targetZ = compound.getDouble("TargetZ")
        if (compound.contains("FireRate")) this.fireRate = compound.getDouble("FireRate")
        if (compound.contains("Spread")) this.spreadAmount = compound.getFloat("Spread")
    }
    
    override fun tick() {
        super.tick()
        
        if (!this.level().isClientSide) {
            // Energy regeneration
            if (this.energy < this.maxEnergy) {
                this.energy = Math.min(this.energy + 1, this.maxEnergy)
            }
            
            // Rapid-fire logic (from SuperbWarfare's GunShootGoal)
            if (this.isFiring && this.energy > 0) {
                performRapidFire()
            }
        }
    }
    
    /**
     * Rapid-fire shooting logic adapted from SuperbWarfare's GunShootGoal
     * Uses shootTimer with cooldown based on fireRate (RPS - Rounds Per Second)
     */
    private fun performRapidFire() {
        val currentTime = System.currentTimeMillis()
        
        // Calculate cooldown in milliseconds (inverse of fireRate)
        val cooldownMs = (1000.0 / this.fireRate).toLong()
        
        // Initialize timer on first fire
        if (this.shootTimer == 0L) {
            this.shootTimer = currentTime
            this.lastFireTime = currentTime - cooldownMs - 1
        }
        
        // Check if enough time has passed for next shot
        if (currentTime - this.lastFireTime >= cooldownMs) {
            fireShot()
            this.lastFireTime = currentTime
            
            // Consume energy
            this.energy = Math.max(0, this.energy - 5)
        }
    }
    
    /**
     * Fire a single shot
     * Creates a projectile/beam towards target position
     */
    private fun fireShot() {
        if (this.level().isClientSide) return
        
        val targetVec = Vec3(this.targetX, this.targetY, this.targetZ)
        val turretPos = this.position()
        
        // Calculate direction
        val direction = targetVec.subtract(turretPos).normalize()
        
        // TODO: Create projectile/energy beam here
        // For now, just spawn particle effect or sound
        // this.level().playSound(null, this.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.BLOCKS, 1.0f, 1.0f)
    }
    
    /**
     * Set target position for turret to aim at
     */
    fun setTarget(x: Double, y: Double, z: Double) {
        this.targetX = x
        this.targetY = y
        this.targetZ = z
    }
    
    /**
     * Get current energy level
     */
    fun getEnergy(): Int = this.energy
    
    /**
     * Set energy level
     */
    fun setEnergy(amount: Int) {
        this.energy = Math.min(Math.max(0, amount), this.maxEnergy)
    }
    
    /**
     * Get max energy capacity
     */
    fun getMaxEnergy(): Int = this.maxEnergy
    
    override fun shouldRenderAtSqrDistance(distance: Double): Boolean = distance < 16384.0

    companion object {
        @JvmField
        val ENERGY: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.INT
        )
        
        @JvmField
        val MAX_ENERGY: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.INT
        )
        
        @JvmField
        val TARGET_X: EntityDataAccessor<Double> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.DOUBLE
        )
        
        @JvmField
        val TARGET_Y: EntityDataAccessor<Double> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.DOUBLE
        )
        
        @JvmField
        val TARGET_Z: EntityDataAccessor<Double> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.DOUBLE
        )
    }
}
