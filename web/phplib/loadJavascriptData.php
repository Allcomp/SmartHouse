<script>
<?php $rsRooms = $database->executeQuery("SELECT * FROM `rooms`;"); ?>
<?php while ($row = $rsRooms->fetch_row()) :?>
    DataStore.addRoom(new Room(<?=($row[0])?>, "<?=($row[1])?>", <?=($row[2])?>));
<?php endwhile; ?>

<?php $rsControls = $database->executeQuery("SELECT * FROM `controls`;"); ?>
<?php while ($row = $rsControls->fetch_row()) :?>
DataStore.addControl(new Control(<?=($row[0])?>, <?=($row[1])?>, "<?=($row[2])?>", "<?=($row[3])?>", <?=($row[4])?>, <?=($row[5])?>));
<?php endwhile; ?>

<?php $rsMacros = $database->executeQuery("SELECT * FROM `macros`;"); ?>
<?php while ($row = $rsMacros->fetch_row()) :?>
DataStore.addMacro(new Macro(<?=($row[0])?>, "<?=($row[1])?>", "<?=($row[3])?>"));
<?php endwhile; ?>

<?php $rsControls2 = $database->executeQuery("SELECT * FROM `controls` ORDER BY `last_time_usage` DESC LIMIT 0,10 ;"); ?>
<?php while ($row = $rsControls2->fetch_row()) :?>
DataStore.addLastUsedControl(new Control(<?=($row[0])?>, <?=($row[1])?>, "<?=($row[2])?>", "<?=($row[3])?>", <?=($row[4])?>, <?=($row[5])?>));
<?php endwhile; ?>
</script>