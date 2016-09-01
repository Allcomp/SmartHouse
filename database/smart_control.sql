-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u1
-- http://www.phpmyadmin.net
--
-- Počítač: localhost
-- Vygenerováno: Čtv 01. zář 2016, 14:28
-- Verze MySQL: 5.5.44
-- Verze PHP: 5.4.41-0+deb7u1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databáze: `smart_control`
--

-- --------------------------------------------------------

--
-- Struktura tabulky `controls`
--

CREATE TABLE IF NOT EXISTS `controls` (
  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,
  `room` int(8) NOT NULL,
  `name` text COLLATE utf8_czech_ci NOT NULL,
  `outputs` text COLLATE utf8_czech_ci NOT NULL,
  `type` int(8) NOT NULL,
  `appliance_type` int(8) NOT NULL,
  `last_time_usage` bigint(64) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci AUTO_INCREMENT=33 ;

--
-- Vypisuji data pro tabulku `controls`
--

INSERT INTO `controls` (`id`, `room`, `name`, `outputs`, `type`, `appliance_type`, `last_time_usage`) VALUES
(1, 1, 'Hlavní osvětlení', '6', 0, 0, 1472659875789),
(2, 1, 'Zrcadlo', '3', 0, 0, 1472659875426),
(3, 1, 'Schodiště', '8', 0, 0, 1472660601941),
(4, 1, 'Mini-B', '69', 0, 0, 1472659872014),
(5, 2, 'Hlavní osvětlení', '26', 0, 0, 1472719137718),
(6, 2, 'Lampa', '21', 0, 0, 1472719141353),
(7, 2, 'TV stěna', '22', 0, 0, 1472659607182),
(8, 2, 'Chodba', '27', 0, 0, 1472660141865),
(9, 2, 'LED pásek', '4', 0, 0, 1472659077606),
(10, 2, 'Zásuvky', '94', 0, 1, 1472736019284),
(11, 3, 'Jídelní stůl', '9', 0, 0, 1472659500396),
(12, 3, 'Ostrůvek', '10', 0, 0, 1472660255121),
(13, 3, 'Osvětlení LED 4', '7', 0, 0, 0),
(14, 3, 'Osvětlení LED 6', '2', 0, 0, 1470999206032),
(15, 3, 'Osvětlení linky', '4', 0, 0, 1472660265674),
(16, 4, 'Hlavní osvětlení', '41', 0, 0, 1472660366670),
(17, 4, 'Stolek L', '42', 0, 0, 1472660295856),
(18, 4, 'Stolek P', '47', 0, 0, 1472660369674),
(19, 4, 'Lampa', '46', 0, 0, 1472416750867),
(21, 5, 'Hlavní osvětlení', '66', 0, 0, 1472660207753),
(22, 5, 'Zrcadlo', '67', 0, 0, 1472660208538),
(23, 5, 'Sprchový kout', '61', 0, 0, 1472660026449),
(24, 5, 'Toaleta', '62', 0, 0, 1472660210107),
(25, 5, 'Vyhřívání vany', '27', 0, 2, 1472660199614),
(26, 5, 'Odvětrávání toalety', '63', 0, 8, 1472662173564),
(27, 6, 'Hlavní osvětlení', '5', 0, 0, 0),
(28, 7, 'Hlavní osvětlení', '1', 0, 0, 1472504626980),
(29, 7, 'Rozvaděč', '116', 0, 0, 1472582300375),
(30, 4, 'Žaluzie', '115,114', 0, 6, 1472728055996),
(31, 5, 'Žaluzie', '118,117', 0, 6, 1472658634776),
(32, 2, 'Žaluzie', '120,119', 0, 6, 1472739886466);

-- --------------------------------------------------------

--
-- Struktura tabulky `macros`
--

CREATE TABLE IF NOT EXISTS `macros` (
  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `controls` text COLLATE utf8_bin NOT NULL,
  `command` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=7 ;

--
-- Vypisuji data pro tabulku `macros`
--

INSERT INTO `macros` (`id`, `name`, `controls`, `command`) VALUES
(1, 'Odcházím', '-6,-3,-8,-103,-38,-34,-39,-13,-129,-9,-10,-7,-2,-4,-64,-65,-70,-22,-100,-101,-95,-96,-27,-28,-5,-31', 'type=value,io=26,value=0;type=value,io=21,value=0;type=value,io=22,value=0;type=value,io=27,value=0;type=value,io=4,value=0;type=value,io=2,value=0;type=value,io=7,value=0;type=value,io=10,value=0;type=value,io=9,value=0;type=value,io=94,value=0'),
(2, 'Jsem doma', '10,129,38,33,69', 'type=value,io=6,value=1;type=value,io=26,value=0;type=pulse,duration=1000,io=119'),
(3, 'Jídlo', '4,9,10', 'type=value,io=4,value=1;type=value,io=9,value=1;type=value,io=10,value=1'),
(4, 'Odpočinek', '', 'type=value,io=21,value=0;type=value,io=22,value=1;type=value,io=27,value=0;type=value,io=4,value=0;type=value,io=9,value=0;type=value,io=10,value=0;type=value,io=7,value=0;type=value,io=2,value=0'),
(5, 'Spánek', '-6,-3,-8,-103,-38,-33,-34,-39,-13,-129,-9,-10,-7,-2,-4,-64,65,70,-69,-22,-100,-101,-95,-96,-27,-28,-5,-1,-31', 'type=value,io=6,value=0;type=value,io=3,value=0;type=value,io=8,value=0;type=value,io=69,value=0;type=value,io=26,value=0;type=value,io=21,value=0;type=value,io=22,value=0;type=value,io=27,value=0;type=value,io=4,value=0;type=value,io=9,value=0;type=value,io=10,value=0;type=value,io=7,value=0;type=value,io=2,value=0;type=value,io=4,value=0;type=value,io=41,value=0;type=value,io=42,value=0;type=value,io=47,value=0;type=value,io=46,value=0;type=value,io=66,value=0;type=value,io=67,value=0;type=value,io=61,value=0;type=value,io=62,value=0;type=value,io=5,value=0;type=value,io=1,value=0;type=value,io=116,value=0;type=pulse,duration=1000,io=115;type=pulse,duration=1000,io=118;type=pulse,duration=1000,io=120'),
(6, 'Tma', '', 'type=value,io=26,value=0;type=value,io=21,value=0;type=value,io=22,value=0;type=value,io=27,value=0;type=value,io=4,value=0;type=pulse,duration=1000,io=44;type=value,io=2,value=0;type=value,io=7,value=0;type=value,io=10,value=0;type=value,io=9,value=0;');

-- --------------------------------------------------------

--
-- Struktura tabulky `rooms`
--

CREATE TABLE IF NOT EXISTS `rooms` (
  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_czech_ci NOT NULL,
  `floor` int(8) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci AUTO_INCREMENT=8 ;

--
-- Vypisuji data pro tabulku `rooms`
--

INSERT INTO `rooms` (`id`, `name`, `floor`) VALUES
(1, 'Vstupní hala', 0),
(2, 'Obývací pokoj', 0),
(3, 'Kuchyně', 0),
(4, 'Ložnice', 0),
(5, 'Koupelna', 0),
(6, 'Sklad', 0),
(7, 'Strojovna', 0);

-- --------------------------------------------------------

--
-- Struktura tabulky `security_systems`
--

CREATE TABLE IF NOT EXISTS `security_systems` (
  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=5 ;

--
-- Vypisuji data pro tabulku `security_systems`
--

INSERT INTO `security_systems` (`id`, `name`) VALUES
(1, 'Celý dům'),
(2, 'Strojovna'),
(3, 'Přední část'),
(4, 'Zadní část');

-- --------------------------------------------------------

--
-- Struktura tabulky `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(16) unsigned NOT NULL AUTO_INCREMENT,
  `username` text COLLATE utf8_bin NOT NULL,
  `password` text COLLATE utf8_bin NOT NULL,
  `permission_level` int(16) NOT NULL DEFAULT '0',
  `last_ip` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=2 ;

--
-- Vypisuji data pro tabulku `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `permission_level`, `last_ip`) VALUES
(1, 'Admin', '4a7d1ed414474e4033ac29ccb8653d9b', 99, '178.17.7.4');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
