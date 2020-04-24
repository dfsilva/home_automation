import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'colors.dart';
import 'letter_spacing.dart';

abstract class HousePyTheme{
  static ThemeData buildTheme() {
    final base = ThemeData.dark();
    return ThemeData(
      scaffoldBackgroundColor: HousePyColors.primaryBackground,
      primaryColor: HousePyColors.primaryBackground,
      focusColor: HousePyColors.focusColor,
      textTheme: _buildRallyTextTheme(base.textTheme),
      inputDecorationTheme: const InputDecorationTheme(
        labelStyle: TextStyle(
          color: HousePyColors.gray,
          fontWeight: FontWeight.w500,
        ),
        filled: true,
        fillColor: HousePyColors.inputBackground,
        focusedBorder: InputBorder.none,
      ),
    );
  }

  static TextTheme _buildRallyTextTheme(TextTheme base) {
    return base
        .copyWith(
      bodyText2: GoogleFonts.robotoCondensed(
        fontSize: 14,
        fontWeight: FontWeight.w400,
        letterSpacing: letterSpacingOrNone(0.5),
      ),
      bodyText1: GoogleFonts.eczar(
        fontSize: 40,
        fontWeight: FontWeight.w400,
        letterSpacing: letterSpacingOrNone(1.4),
      ),
      button: GoogleFonts.robotoCondensed(
        fontWeight: FontWeight.w700,
        letterSpacing: letterSpacingOrNone(2.8),
      ),
      headline5: GoogleFonts.eczar(
        fontSize: 40,
        fontWeight: FontWeight.w600,
        letterSpacing: letterSpacingOrNone(1.4),
      ),
    )
        .apply(
      displayColor: Colors.white,
      bodyColor: Colors.white,
    );
  }
}


