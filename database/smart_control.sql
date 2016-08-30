-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u1
-- http://www.phpmyadmin.net
--
-- Počítač: localhost
-- Vygenerováno: Pon 15. srp 2016, 09:11
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci AUTO_INCREMENT=30 ;

--
-- Vypisuji data pro tabulku `controls`
--

INSERT INTO `controls` (`id`, `room`, `name`, `outputs`, `type`, `appliance_type`, `last_time_usage`) VALUES
(1, 1, 'Hlavní osvětlení', '6', 0, 0, 1470930713674),
(2, 1, 'Zrcadlo', '3', 0, 0, 1470925929344),
(3, 1, 'Schodiště', '8', 0, 0, 1469173023772),
(4, 1, 'Mini-B', '103', 0, 0, 1469183705314),
(5, 2, 'Hlavní osvětlení', '38', 0, 0, 1470999428816),
(6, 2, 'Lampa', '33', 0, 0, 1470771729397),
(7, 2, 'TV stěna', '34', 0, 0, 1470930747788),
(8, 2, 'Chodba', '39', 0, 0, 1470753975366),
(9, 2, 'LED pásek', '13', 0, 0, 1469904838449),
(10, 2, 'Zásuvky', '129', 0, 1, 1469904842040),
(11, 3, 'Jídelní stůl', '9', 0, 0, 1470999208767),
(12, 3, 'Ostrůvek', '10', 0, 0, 1470999215143),
(13, 3, 'Osvětlení LED 4', '7', 0, 0, 0),
(14, 3, 'Osvětlení LED 6', '2', 0, 0, 1470999206032),
(15, 3, 'Osvětlení linky', '4', 0, 0, 1470999408567),
(16, 4, 'Hlavní osvětlení', '64', 0, 0, 1470769186211),
(17, 4, 'Stolek L', '65', 0, 0, 1470769185943),
(18, 4, 'Stolek P', '70', 0, 0, 1470769185617),
(19, 4, 'Lampa', '69', 0, 0, 1470771722063),
(21, 5, 'Hlavní osvětlení', '100', 0, 0, 0),
(22, 5, 'Zrcadlo', '101', 0, 0, 0),
(23, 5, 'Sprchový kout', '95', 0, 0, 0),
(24, 5, 'Toaleta', '96', 0, 0, 0),
(25, 5, 'Vyhřívání vany', '27', 0, 0, 0),
(26, 5, 'Odvětrávání toalety', '28', 0, 0, 0),
(27, 6, 'Hlavní osvětlení', '5', 0, 0, 0),
(28, 7, 'Hlavní osvětlení', '1', 0, 0, 1469179142523),
(29, 7, 'Rozvaděč', '31', 0, 0, 1469179140573);

-- --------------------------------------------------------

--
-- Struktura tabulky `macros`
--

CREATE TABLE IF NOT EXISTS `macros` (
  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `controls` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=6 ;

--
-- Vypisuji data pro tabulku `macros`
--

INSERT INTO `macros` (`id`, `name`, `controls`) VALUES
(1, 'Odcházím', '-6,-3,-8,-103,-38,-34,-39,-13,-129,-9,-10,-7,-2,-4,-64,-65,-70,-22,-100,-101,-95,-96,-27,-28,-5,-31'),
(2, 'Jsem doma', '10,129,38,33,69'),
(3, 'Jídlo', '4,9,10'),
(4, 'Odpočinek', '-38,-9,-10,-7,2,-4,33,34'),
(5, 'Spánek', '-6,-3,-8,-103,-38,-33,-34,-39,-13,-129,-9,-10,-7,-2,-4,-64,65,70,-69,-22,-100,-101,-95,-96,-27,-28,-5,-1,-31');

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
